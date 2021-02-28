# backend-server
Backend server for scpk app

# Concepts
- **Payment Request** - represents request of user to be paid,
    from another set of users, at the moment each user is charged equally
- **User Balance** - is a container for agregation of bill to pay for each other
    user participating in *Payment Group*
    - **User Saldo** - represents single user and amount to be paid for him/her,
        each *User Balance* has its own set of user saldos
- **Payment Group** - describe set of users and bills to be paid among them

# API Documentation
- [PaymentAPI](https://documenter.getpostman.com/view/12072151/TWDdiDhH)
- [UserAPI](https://documenter.getpostman.com/view/12072151/TWDdiDma)

## Techs
- Spring MVC
- Spring Data
- Spring HATEOS
- Spring Repositories
- Hibernates

## Build
```bash
mvn spring-boot:run
```

# Considerations
- Each *User Balance* contains *User Saldo* for the owner of user balance - this means
    that one must pay for itself, but it is not a case, as this feature is 
    solely for implementation purpose
- Each payment request should contain requesting user as charged, it is important for
    system performance and stability
    
# Links
- [Docker](https://hub.docker.com/repository/docker/ptylczynski/sckp-backend)
