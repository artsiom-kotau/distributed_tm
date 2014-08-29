package com.cs.distr;

import com.cs.distr.api.ClientZeroServiceImpl;
import com.cs.distr.api.entity.UserDto;
import com.cs.distr.shared.api.ClientService;
import com.cs.distr.shared.api.SomeObject;
import com.cs.distr.shared.api.entity.UserAccountEntity;
import com.cs.distr.shared.api.entity.UserEntity;
import com.cs.distr.shared.api.transaction.store.TidCash;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import org.apache.commons.codec.binary.Hex;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.UUID;

/**
 * @author: artsiom.kotov
 */
public class RunnerClient0 {
    public static void main(String[] args) throws Exception {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("client0AppConfig.xml");
        ClientZeroServiceImpl clientService = (ClientZeroServiceImpl) appContext.getBean("zeroService");
        ClientService firstClientService = clientService.getFirstService();
        ClientService secondClientService = clientService.getSecondService();
        int amount = 50;
        int i = 0;
        String uid = UUID.randomUUID().toString();
        UserDto userDto = new UserDto("someAction_"+uid,"someAction","SomeAction", "someAction@mail.com");
        String hashString = userDto.getFirstName() + userDto.getLastName() + userDto.getEmail();
        UserAccountEntity userAccountEntity = new UserAccountEntity(userDto.getUid(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                clientService.hashString(hashString));

        while(i<amount) {
            userAccountEntity.setId("success_"+i);
            secondClientService.someAction(userAccountEntity,false);
            userAccountEntity.setId("negative_"+i);
            try {
                secondClientService.someAction(userAccountEntity,true);
            } catch (Exception exc) {
                System.out.println(userAccountEntity.getId() + "is rolled back");
            }
            i++;
        }
        /*while(i<amount) {
            String uid = String.valueOf(UUID.randomUUID().getMostSignificantBits());
            firstClientService.create(new UserEntity(uid,"firstClient","firstClient","firstClient"));
            createTest(clientService);
            updateTest(clientService);
            deleteTest(clientService);
            firstClientService.delete(new UserEntity(uid,"firstClient","firstClient","firstClient"));
            i++;
        } */
        //System.out.println("all done");
    }

    private static void createTest(ClientZeroServiceImpl clientService) {
        try {
            String uid = String.valueOf(UUID.randomUUID().getMostSignificantBits());
            {
               System.out.println("Success create test");
                UserDto successUserDto = new UserDto("success_"+uid,"artsiom","kotau", "success@mail.com");
                clientService.create(successUserDto, false);
                System.out.println("Success create test done");
            }
            {
                System.out.println("Negative create test");
                UserDto badUserDto = new UserDto("bad_"+uid,"ivan","ivanou", "ivan@mail.com");
                clientService.create(badUserDto, true);
                System.out.println("Negative create test done");
            }

        } catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
    }

    private static void updateTest(ClientZeroServiceImpl clientService) {
        try {
            {
                System.out.println("Success update test");
                clientService.update(null, false);
                System.out.println("Success update test done");
            }
            {
                System.out.println("Negative update test");
                clientService.update(null, true);
                System.out.println("Negative update test done");
            }

        } catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
    }

    private static void deleteTest(ClientZeroServiceImpl clientService) {
        try {

            {
                System.out.println("Negative delete test");
                clientService.delete(null, true);
                System.out.println("Negative delete test done");
            }
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
            {
                System.out.println("Success delete test");
                clientService.delete(null, false);
                System.out.println("Success delete test done");
            }
    }
}
