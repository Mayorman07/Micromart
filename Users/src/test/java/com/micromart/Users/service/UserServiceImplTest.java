package com.micromart.Users.service;

import com.micromart.constants.Status;
import com.micromart.entities.User;
import com.micromart.exceptions.ConflictException;
import com.micromart.messaging.MessagePublisher;
import com.micromart.models.data.UserCreatedEventDto;
import com.micromart.models.data.UserDto;
import com.micromart.repositories.RoleRepository;
import com.micromart.repositories.UserRepository;
import com.micromart.services.RefreshTokenService;
import com.micromart.services.UserServiceImpl;
import com.micromart.utils.JwtUtils;
import com.micromart.utils.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private ModelMapper modelMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RoleRepository roleRepository;
    @Mock private UserRepository userRepository;
    @Mock private MessagePublisher messagePublisher;
    @Mock private TokenService tokenService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private JwtUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // Setup DTO (Input)
        userDto = new UserDto();
        userDto.setEmail("test@micromart.com");
        userDto.setPassword("SecurePass123!");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");

        // Setup Entity (Database Mock)
        mockUser = new User();
        mockUser.setUserId("user-uuid-1234");
        mockUser.setEmail("test@micromart.com");
        mockUser.setEncryptedPassword("encoded_password");
        mockUser.setStatus(Status.ACTIVE);
        mockUser.setRoles(new ArrayList<>());
    }

    // ==========================================
    // CREATE USER TESTS
    // ==========================================

    @Test
    @DisplayName("createUser - Success: Should encrypt password, generate token, and publish event")
    void createUser_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(modelMapper.map(userDto, User.class)).thenReturn(mockUser);
        when(tokenService.generateToken()).thenReturn("verification-token-999");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(modelMapper.map(mockUser, UserDto.class)).thenReturn(userDto);

        // Act
        UserDto result = userService.createUser(userDto);

        // Assert
        assertNotNull(result);

        // Verify Security/Business Rules
        verify(passwordEncoder, times(1)).encode("SecurePass123!");
        verify(userRepository, times(1)).save(mockUser);

        // Capture the event to ensure the message broker gets the right data
        ArgumentCaptor<UserCreatedEventDto> eventCaptor = ArgumentCaptor.forClass(UserCreatedEventDto.class);
        verify(messagePublisher, times(1)).sendUserCreatedEvent(eventCaptor.capture());

        UserCreatedEventDto capturedEvent = eventCaptor.getValue();
        assertEquals("test@micromart.com", capturedEvent.getEmail());
        assertEquals("verification-token-999", capturedEvent.getVerificationToken());
    }

    @Test
    @DisplayName("createUser - Failure: Should throw ConflictException if email exists")
    void createUser_EmailExists_ThrowsConflictException() {
        // Arrange
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(mockUser));

        // Act & Assert
        assertThrows(ConflictException.class, () -> userService.createUser(userDto));

        // Verify we aborted before doing anything expensive
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(messagePublisher, never()).sendUserCreatedEvent(any());
    }

    // ==========================================
    // VERIFY USER TESTS
    // ==========================================

    @Test
    @DisplayName("verifyUser - Success: Should activate user and clear token")
    void verifyUser_Success() {
        // Arrange
        mockUser.setStatus(Status.INACTIVE);
        mockUser.setVerificationToken("valid-token");
        when(userRepository.findByVerificationToken("valid-token")).thenReturn(Optional.of(mockUser));

        // Act
        boolean isVerified = userService.verifyUser("valid-token");

        // Assert
        assertTrue(isVerified);
        assertEquals(Status.ACTIVE, mockUser.getStatus());
        assertNull(mockUser.getVerificationToken()); // Token should be wiped
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    @DisplayName("verifyUser - Failure: Should return false for invalid token")
    void verifyUser_InvalidToken_ReturnsFalse() {
        // Arrange
        when(userRepository.findByVerificationToken("invalid-token")).thenReturn(Optional.empty());

        // Act
        boolean isVerified = userService.verifyUser("invalid-token");

        // Assert
        assertFalse(isVerified);
        verify(userRepository, never()).save(any(User.class));
    }

    // ==========================================
    // LOAD USER BY USERNAME (SECURITY) TESTS
    // ==========================================

    @Test
    @DisplayName("loadUserByUsername - Success: Should return CustomUserDetails for active user")
    void loadUserByUsername_Success() {
        // Arrange
        mockUser.setStatus(Status.ACTIVE);
        when(userRepository.findByEmail("test@micromart.com")).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = userService.loadUserByUsername("test@micromart.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals("test@micromart.com", userDetails.getUsername());
        assertEquals("encoded_password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    @DisplayName("loadUserByUsername - Failure: Should throw DisabledException for inactive user")
    void loadUserByUsername_InactiveUser_ThrowsDisabledException() {
        // Arrange
        mockUser.setStatus(Status.INACTIVE);
        when(userRepository.findByEmail("test@micromart.com")).thenReturn(Optional.of(mockUser));

        // Act & Assert
        assertThrows(DisabledException.class, () -> userService.loadUserByUsername("test@micromart.com"));
    }

    @Test
    @DisplayName("loadUserByUsername - Failure: Should throw UsernameNotFoundException if email not found")
    void loadUserByUsername_NotFound_ThrowsUsernameNotFoundException() {
        // Arrange
        when(userRepository.findByEmail("ghost@micromart.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("ghost@micromart.com"));
    }
}
