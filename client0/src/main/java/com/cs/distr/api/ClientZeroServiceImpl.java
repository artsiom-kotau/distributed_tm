package com.cs.distr.api;

import com.cs.distr.api.entity.UserDto;
import com.cs.distr.shared.api.ClientService;
import com.cs.distr.shared.api.ITransactionalEntity;
import com.cs.distr.shared.api.entity.UserAccountEntity;
import com.cs.distr.shared.api.entity.UserEntity;
import com.cs.distr.shared.api.transaction.store.TidCash;
import org.apache.commons.codec.binary.Hex;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;

/**
 * @author: artsiom.kotov
 */
public class ClientZeroServiceImpl {

    private static final long serialVersionUID = 6960481547739173285L;

    private ClientService firstService;
    private ClientService secondService;

    public ClientService getFirstService() {
        return firstService;
    }

    public void setFirstService(ClientService firstService) {
        this.firstService = firstService;
    }

    public ClientService getSecondService() {
        return secondService;
    }

    public void setSecondService(ClientService secondService) {
        this.secondService = secondService;
    }

    @Transactional
    public UserDto create(UserDto userDto, boolean negative) {
        try {
            UserEntity userEntity = new UserEntity(userDto.getUid(), userDto.getFirstName(), userDto.getLastName(), userDto.getEmail());
            String hashString = userDto.getFirstName() + userDto.getLastName() + userDto.getEmail();
            UserAccountEntity userAccountEntity =
                    new UserAccountEntity(userDto.getUid(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                            hashString(hashString));
            firstService.create(userEntity);
            secondService.create(userAccountEntity);
            if (negative) {
                throw new RuntimeException(
                        userEntity.getId() + "; exception secondStep; Tid: " + TidCash.getTid(Thread.currentThread().getId()).getUuid());
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
        return userDto;
    }

    @Transactional
    public ITransactionalEntity update(ITransactionalEntity entity, boolean negative) {
        try {
            List<UserEntity> users = firstService.findAll();
            for (UserEntity userEntity : users) {
                userEntity.setEmail(negative ? "negativeEmail" : "successEmail");
                UserAccountEntity userAccountEntity = (UserAccountEntity) secondService.findById(userEntity.getId());
                if (userAccountEntity != null) {
                    userAccountEntity.setPassword(negative ? "negativePassword" : "successPassword");
                    secondService.update(userAccountEntity);
                }
                firstService.update(userEntity);
                if (negative) {
                    throw new RuntimeException(
                            userEntity.getId() + "; exception secondStep; Tid: " + TidCash.getTid(Thread.currentThread().getId()).getUuid());
                }
            }

        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
        return null;
    }

    @Transactional
    public ITransactionalEntity delete(ITransactionalEntity entity, boolean negative) {
        try {
            List<UserEntity> users = firstService.findAll();
            for (UserEntity userEntity : users) {
                firstService.delete(userEntity);
                UserAccountEntity userAccountEntity = (UserAccountEntity) secondService.findById(userEntity.getId());
                if (userAccountEntity != null) {
                    secondService.delete(userAccountEntity);
                }
                if (negative) {
                    throw new RuntimeException(
                            userEntity.getId() + "; exception secondStep; Tid: " + TidCash.getTid(Thread.currentThread().getId()).getUuid());
                }
            }

        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
        return null;
    }

    public ITransactionalEntity findById(Object id) {
        return null;
    }

    public String hashString(String string) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        byte[] buffer = string.getBytes();
        md.update(buffer);
        byte[] digest = md.digest();
        return new String(Hex.encodeHex(digest));
    }
}
