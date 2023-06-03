package pt.isel.iot_data_server.security

/*
class SaltTests {
    private val role = Role.USER

    @Test
    fun `verify if two equal passwords are stored the same`(){
        testWithTransactionManagerAndRollback { transactionManager ->
            val salt = SaltPasswordOperations(transactionManager)
            val emailService = EmailManager()
            val service = UserService(transactionManager,salt,emailService)

            //create user
            val email1 = "testSubject@email.com"
            val email2 = "testSubject2@email.com"
            val pass = "LKMSDOVCJ09Jouin09JN@"
            service.createUser(email1, pass, role)
            service.createUser(email2, pass, role)


            val userStoredPassword = service.getUserByEmail(email1)
            val user2StoredPassword = service.getUserByEmail(email2)

            assertFalse("Password is not the same", userStoredPassword == user2StoredPassword)
        }


    }


    @Test
    fun `verify correct password`(){
        testWithTransactionManagerAndRollback { transactionManager ->
            val salt = SaltPasswordOperations(transactionManager)
            val service = UserService(transactionManager,salt)

            //create user
            val pass = "LKMSDOVCJ09Jouin09JN@"
            val newUser = UserInfo("userGood", pass,"testSubject@email.com", role)
            service.createUser(newUser)

            //stored user password
            val user = service.getUserByEmail(newUser.email)?.userInfo
            if(user == null) throw Exception("User not found")

            //verify password
            val resultOfVerify = salt.verifyPassword(user.username,pass)//storedPassword is not null
            assertTrue("Password is correct", resultOfVerify)
        }
    }

    @Test
    fun `verify incorrect password`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val salt = SaltPasswordOperations(transactionManager)
            val service = UserService(transactionManager,salt)

            //create user
            val pass = "LKMSDOVCJ09Jouin09JN@"
            val newUser = UserInfo("userGood", pass,"testSubject@email.com", role)
            service.createUser(newUser)

            //stored user password
            val user = service.getUserByEmail(newUser.email)?.userInfo
            if(user == null) throw Exception("User not found")

            val fakePass = "LKMSDOVCJ09Jouin0fake"
            //verify password
            val resultOfVerify = salt.verifyPassword(user.username,fakePass)//storedPassword is not null
            assertFalse("Password is correct", resultOfVerify)
        }
    }

    @Test
    fun `save a valid salt`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val saltPasswordOperations = SaltPasswordOperations(transactionManager)
            val id = UUID.randomUUID().toString()
            val password = "foefmefew43ok@skdkK"
            saltPasswordOperations.saltAndHashPass(password,id)
            val salt = saltPasswordOperations.getSalt(id)
            Assertions.assertTrue(salt.isNotEmpty())
        }
    }

}*/