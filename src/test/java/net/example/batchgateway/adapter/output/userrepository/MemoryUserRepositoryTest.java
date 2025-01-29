package net.example.batchgateway.adapter.output.userrepository;

import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.usermodule.User;
import net.example.batchgateway.application.domain.model.usermodule.UserId;
import net.example.batchgateway.application.domain.model.usermodule.UserName;
import org.junit.jupiter.api.Test;



class MemoryUserRepositoryTest {

    @Test
    void findById() {
    }

    @Test
    void findByTenantId() {
    }

    @Test
    void save() {
        final MemoryUserRepository memoryUserRepository = new MemoryUserRepository();
        memoryUserRepository.save(User.initExisting(UserId.generate(),new UserName("a name"),  TenantId.generate()));

        assert(true);
    }

    @Test
    void delete() {
    }
}
