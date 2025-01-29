package net.example.batchgateway.application.service;

import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.usermodule.User;
import net.example.batchgateway.application.domain.model.usermodule.UserName;
import net.example.batchgateway.application.domain.model.events.ApplicationDeletedEvent;
import net.example.batchgateway.application.domain.model.events.DomainEvent;
import net.example.batchgateway.application.domain.model.events.UserDeletedEvent;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.ServiceError;
import net.example.batchgateway.application.port.output.DomainEventRepositoryPort;
import net.example.batchgateway.application.port.output.UserRepositoryPort;
import net.example.utils.dichotomy.Empty;
import net.example.utils.dichotomy.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAdapterTest {
    private final UserRepositoryPort userRepository = mock(UserRepositoryPort.class);
    private final DomainEventRepositoryPort domainEventRepository = mock(DomainEventRepositoryPort.class);

    private final UserAdapter userAdapter = new UserAdapter(userRepository, domainEventRepository);

    @Test
    void saveSuccesSavingAndSavingEvents() {
        final User user = User.create(new UserName("userName"),
                TenantId.generate()
        );

        assertFalse(user.domainEvents().isEmpty());

        when(userRepository.save(user)).thenReturn(Result.ofOK(user));

        when(domainEventRepository.save(user.domainEvents())).thenReturn(Result.ofOK());

        final Result<User, GeneralError> result = userAdapter.save(user);


        assertNotNull(result);
        assertTrue(result.isOK());
        assertEquals(user, result.expect());

        assertTrue(user.domainEvents().isEmpty());

        verify(userRepository, times(1)).save(any());
        verify(domainEventRepository, times(1)).save(anyList());

    }

    @Test
    void saveFailSavingDontSendEvents() {
        final User user = User.create(new UserName("userName"),
                TenantId.generate()
        );

        assertFalse(user.domainEvents().isEmpty());

        when(userRepository.save(user)).thenReturn(Result.ofErr(new ServiceError.DatabaseError(new Throwable())));

        when(domainEventRepository.save(user.domainEvents())).thenReturn(Result.ofOK());

        final Result<User, GeneralError> result = userAdapter.save(user);


        assertNotNull(result);
        assertTrue(result.isErr());

        assertFalse(user.domainEvents().isEmpty());

        verify(userRepository, times(1)).save(any());
        verify(domainEventRepository, times(0)).save(anyList());

    }

    @Test
    void deleteSuccessfullyProducesEvent() {
        final User user = User.create(new UserName("userName"),
                TenantId.generate()
        );

        when(userRepository.findById(user.getId())).thenReturn(Result.ofOK(Optional.of(user)));
        when(userRepository.delete(user.getId())).thenReturn(Result.ofOK());

        when(domainEventRepository.save(any(DomainEvent.class))).thenReturn(Result.ofOK());

        final ArgumentCaptor<UserDeletedEvent> captor = ArgumentCaptor.forClass(UserDeletedEvent.class);

        final Result<Empty, GeneralError> result = userAdapter.delete(user.getId());


        assertNotNull(result);
        assertTrue(result.isOK());

        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).delete(any());
        verify(domainEventRepository, times(1)).save(captor.capture());

        final UserDeletedEvent captorEvent = captor.getValue();

        assertEquals(user.getId(), captorEvent.getUserId());
    }

    @Test
    void deleteNonExistingDoesNothing() {
        final User user = User.create(new UserName("userName"),
                TenantId.generate()
        );

        when(userRepository.findById(user.getId())).thenReturn(Result.ofOK(Optional.empty()));
        when(userRepository.delete(user.getId())).thenReturn(Result.ofOK());

        when(domainEventRepository.save(any(DomainEvent.class))).thenReturn(Result.ofOK());

        final ArgumentCaptor<ApplicationDeletedEvent> captor = ArgumentCaptor.forClass(ApplicationDeletedEvent.class);

        final Result<Empty, GeneralError> result = userAdapter.delete(user.getId());


        assertNotNull(result);
        assertTrue(result.isOK());

        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(0)).delete(any());
        verify(domainEventRepository, times(0)).save(captor.capture());
    }

    @Test
    void deleteSaveFailsReturnsErrorAndDoNotProduceEvent() {
        final User user = User.create(new UserName("userName"),
                TenantId.generate()
        );

        when(userRepository.findById(user.getId())).thenReturn(Result.ofOK(Optional.of(user)));
        when(userRepository.delete(user.getId())).thenReturn(Result.ofErr(new ServiceError.DatabaseError(new Throwable())));

        when(domainEventRepository.save(any(DomainEvent.class))).thenReturn(Result.ofOK());

        final ArgumentCaptor<UserDeletedEvent> captor = ArgumentCaptor.forClass(UserDeletedEvent.class);

        final Result<Empty, GeneralError> result = userAdapter.delete(user.getId());


        assertNotNull(result);
        assertTrue(result.isErr());

        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).delete(any());
        verify(domainEventRepository, times(0)).save(captor.capture());

    }

}
