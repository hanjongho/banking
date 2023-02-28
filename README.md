## 뱅킹서버 구조

<img width="1251" alt="image" src="https://user-images.githubusercontent.com/4801524/221780819-ed5aab5e-7c1f-40a2-aabb-6fc2245234e8.png">

## DB ERD

<img width="405" alt="image" src="https://user-images.githubusercontent.com/4801524/221780872-011a7baf-f49b-4cce-8e89-846e4a2a04f6.png">

## API 스펙
<details>
<summary>펼치기</summary>

### 공통 예외
  ```json
  400 BAD REQUEST
  {
    "success": false,
    "response": null,
    "error": {
        "message": "친구로 등록되어 있지 않습니다",
        "status": 400
    }
  }

  401 UNAUTHORIZED
  {
    "success": false,
    "response": null,
    "error": {
      "message": "권한이 없습니다",
      "status": 401
    }
  }

  404 NOT FOUND
  {
    "success": false,
    "response": null,
    "error": {
        "message": "해당 유저 정보를 찾을 수 없습니다",
        "status": 404
    }
  }
  ```


* 계좌이체 API
  
  ```POST /api/account```
  
  ```json
  // REQUEST
  {
    "receiverAccountNo": "396-477-013208",
    "transferAmount": 1000
  }
  
  // RESPONSE
  200 OK
  {
    "success": true,
    "response": {
        "receiverLoginId": "numble-tester",
        "transferAmount": 30000
    },
    "error": null
  }
  400 BAD REQUEST
  {
    "success": false,
    "response": null,
    "error": {
        "message": "계좌의 잔액이 부족합니다.",
        "status": 400
    }
  }
  ```
* 계좌조회 API (내 계좌만 조회가능)
  
  ```GET /api/account```

  ```Bearer {JWT_TOKEN}```
  ```json
  // RESPONSE
  200 OK
  {
    "success": true,
    "response": {
        "accountId": "678-882-860801",
        "amount": 70000
    },
    "error": null
  }
  ```
* 회원가입 API
  
  ```POST  /api/member/join```
  ```json
  // REQUEST
  {
    "loginId": "numble-hanjongho",
    "password": "12345678",
    "amount": 100000
  }
  
  // RESPONSE
  200 OK
  {
    "success": true,
    "response": {
        "loginId": "numble-hanjongho",
        "authorityDtoSet": [
            {
                "authorityName": "ROLE_MEMBER"
            }
        ]
    },
    "error": null
  }
  ```

* 인증 API
  
  ```POST  /api/member/authenticate```
  ```json
  // REQUEST
  {
    "loginId": "numble-hanjongho",
    "password": "12345678"
  }
  
  // RESPONSE
  {
    "success": true,
    "response": {
        "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJudW1ibGUtaGFuam9uZ2hvIiwiYXV0aCI6IlJPTEVfTUVNQkVSIiwiZXhwIjoxNjc3NjA3NDM1fQ.BzDBSkKC5PVgli1ivie7mIdaX1U8AwIzjsZ8riCsi4SksjfohBwmdVBXg6PI6meX6rpYrrdztg2KdecUUu1vxw"
    },
    "error": null
  }
  ```
* 내 친구 목록 조회 API
  
  ```GET  /api/member/connections```
  
  ```Bearer {JWT_TOKEN}```
  ```json
  // RESPONSE
  200 OK
  {
    "success": true,
    "response": [
        {
            "loginId": "numble-tester"
        }
    ],
    "error": null
  }
  ```
* 친구 추가 API
  
  ```POST  /api/member/connections/{friendId}```
  
  ```Bearer {JWT_TOKEN}```
  ```json
  200 OK
  {
    "success": true,
    "response": {
        "loginId": "numble-tester"
    },
    "error": null
  }
  ```
* 나의 권한 조회 API
  
  ```GET  /api/member```
  
  ```Bearer {JWT_TOKEN}```
  ```json
  200 OK
  {
    "success": true,
    "response": {
        "loginId": "numble-hanjongho",
        "authorityDtoSet": [
            {
                "authorityName": "ROLE_MEMBER"
            }
        ]
    },
    "error": null
  }
  ```
* 회원 권한 조회 API
  
  ```GET  /api/member/{loginId}```
  
  ```Bearer {JWT_TOKEN}```
  ```json
  200 OK
  {
    "success": true,
    "response": {
        "loginId": "numble-hanjongho",
        "authorityDtoSet": [
            {
                "authorityName": "ROLE_MEMBER"
            }
        ]
    },
    "error": null
  }
  ```

</details>

## 기술적 Issue
<details>
<summary>펼치기</summary>

### 1. 동시성이슈
조건 : 하나의 계좌에 동시에 돈이 입금되는 상황이 발생할 수 있습니다.

조건을 충족을 위해 동시성 테스트를 진행해보기로 했습니다. 이체 로직을 테스트 하기 위해 2명을 회원가입하고 이체하는 로직을 작성했는데, ```transfer()``` 메소드에서 회원을 찾지 못해 예외가 발생되었다. 

```java
@Test
@DisplayName("성공 - 100개 스레드에서 동시에 입금 - Pessimistic Lock")
void success_total_100_threads() throws Exception {
    //given
    String senderId = "sender";
    String senderPw = "5678";
    MemberDto memberDto1 = MemberDto.builder()
            .loginId(senderId)
            .password(senderPw)
            .amount(1000L).build();
    memberService.join(memberDto1);

    String receiverId = "receiver";
    String receiverPw = "1234";

    MemberDto memberDto2 = MemberDto.builder()
            .loginId(receiverId)
            .password(receiverPw)
            .amount(0L).build();
    memberService.join(memberDto2);

    memberService.addConnection(senderId, receiverId);

    //when
    int numberOfThreads = 100;
    ExecutorService service = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);

    for (int i = 0; i < numberOfThreads; i++) {
        service.execute(() -> {
            transaction.execute((status -> {
                accountService.transfer(senderId, 1L, receiverId);
                latch.countDown();
                return null;
            }));
        });
    }
    latch.await();
    Thread.sleep(1000);

    //then
    Member sender = memberRepository.findByLoginId(senderId).get();
    Member receiver = memberRepository.findByLoginId(receiverId).get();

    assertEquals(1000L - numberOfThreads, sender.getAccount().getAmount());
    assertEquals(0 + numberOfThreads, receiver.getAccount().getAmount());
}
```
100개의 스레드가 1번 회원 계좌에서 2번 회원 계좌로 1원씩 이체되어 결과적으로 1번 회원 계좌에는 900원, 2번 회원 계좌에는 100원이 있을 것으로 예상했다.

#### 발생한 문제 1번
<img width="837" alt="image" src="https://user-images.githubusercontent.com/4801524/221824633-b851750e-c13d-405a-87a6-9b5e25d94aa8.png">

회원을 찾지 못해 ```MEMBER_NOT_FOUND``` 예외가 던져졌다.

#### 생각해본 방법
* JPA를 공부할 때, ```save()```를 실행하면 바로 db에 값이 저장되는게 아닌 영속성 컨텍스트에 우선 저장이 되고, 트랜잭션이 모두 끝나고 ```flush()```되는 시점에 쌓인 쿼리들이 모두 db에 반영된다고 학습했었던 것이 생각났다. 그렇기 때문에 아직 db에는 반영되지 않아서, 회원이 조회되지 않았던 것이다. 
테스트를 위해 Service 계층의 ```@Transactional``` Propagation 설정 값을 바꾸는 것이 좋지 않다고 생각되어서 트랜잭션을 분리해보기로 했다.

<img width="706" alt="image" src="https://user-images.githubusercontent.com/4801524/221831121-3c98b02b-9019-4581-9211-2a2b3660c138.png">

TransactionTemplate을 이용해서 트랜잭션을 분리할 수 있었다.
```java
transaction.execute((status -> memberService.join(memberDto1)));
```

#### 발생한 문제 2번
```@Transactional + synchronized``` 메소드 사용 시
<img width="953" alt="image" src="https://user-images.githubusercontent.com/4801524/221815813-197f7ba8-67c7-43c4-985e-9c0be37a99b0.png">

<img width="1141" alt="image" src="https://user-images.githubusercontent.com/4801524/221816281-25bc424d-c85c-49e1-8b00-710ed006df18.png">
트랜잭션 종료 시점에 Dirty checking을 통해 값을 갱신하는데 이 더티 체킹 과정이 시작하기전에 다른 스레드가 synchronized 메소드에 진입을 해버려서 데이터 정합성이 맞지 않는 문제가 발생했다. 

synchronized 근본적인 한계점
* `@Transactional`과 사용했을 때 트랜잭션이 커밋되기 전에 다른 스레드가 진입해서 이체 메소드가 호출되면 반영되지 않은 값을 읽을 수 있다.
* synchronized는 1개의 프로세스에서만 적용되기 때문에 서버가 여러대가 있는 경우 결국 race condition이 발생한다.

#### 생각해본 방법
1. DB를 이용한 방법 - Pessimistic Lock (비관적 락)
    <details>
    <summary>펼치기</summary>
   
    ```java
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.loginId =:loginId")
    Optional<Member> findByWithPessimisticLock(@Param("loginId") String loginId);
    ```
    * 다른 트랜잭션이 특정 row 의 Lock 을 얻는것을 방지한다. A 트랜잭션이 끝날때까지 기다렸다가 B 트랜잭션이 lock 을 획득한다.
    * 특정 row를 update 하거나 delete 할 수 있다.
    * 일반 select 는 별다른 Lock 이 없기때문에 조회는 가능하다.
    
    </details>
2. DB를 이용한 방법 - Optimistic Lock (낙관적 락)
    <details>
    <summary>펼치기</summary>
   
    ```java
    public interface MemberRepository extends JpaRepository<Account, Long> {
  
      @Lock(value = LockModeType.OPTIMISTIC)
      @Query("select a from Account a where a.id =:id")
      Account findByIdWithOptimisticLock(Long id);
  
    }
    // 아래는 OptimisticLockAccountFacade 클래스, 서비스를 호출하고, 수정사항이 발생하는 상황을
    // 고려해서 재 호출해주는 과정이 필요하다.
    
    public void transfer(Long id, Long amount) throws InterruptedException {
      while (true) {
        try {
          optimisticLockAccountService.transfer(id, amount);
          break ;
        } catch (Exception e) {
          Thread.sleep(50);
        }
      }
    }
    ```
   * Lock을 걸지않고 문제가 발생할 때 처리한다. 대표적으로 version column 을 만들어서 해결하는 방법이 있다. 읽을 때 version을 가져오고 수정해서 update query날릴 때 version를 통해 where 절을 걸어서 수정사항이 있는지 확인한다. 수정사항이 있으면 application에서 다시 읽은 후 작업을 하는 로직을 개발자가 직접 구현해줘야 한다.
   
    </details>
3. Named Lock
    <details>
    <summary>펼치기</summary>
   
    ```java
    // LockRepository.interface
    public interface LockRepository extends JpaRepository<Stock, Long> {
    
      @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
      void getLock(String key);
    
      @Query(value = "select release_lock(:key)", nativeQuery = true)
      void releaseLock(String key);
    
    }
    
    // NamedLockStockFacade..Facade.java
    @Component
    public class NamedLockStockFacade {
    
        ...
    
      @Transactional
      public void decrease(Long id, Long quantity) {
        try {
          lockRepository.getLock(id.toString());
          stockService.decrease(id, quantity);
        } finally {
          lockRepository.releaseLock(id.toString());
        }
      }
    }
    ```
    * 이름을 가지는 Lock을 획득한다. 해당 Lock 은 다른 세션에서 획득 및 해제가 불가능합니다. 종료될 때 Lock을 해제 해줘야 한다. Pessimistic은 직접 해당 row에 Lock을 건다면, Named Lock은 별도의 Lock 테이블을 만들고 key를 통해 Lock을 관리한다. 로직 전후로 getLock, releaseLock 과정이 필요해서 facade 클래스도 필요하다. 주로 분산락을 구현할 때 사용한다. 트랜잭션 종료시에 Lock, Session  관리를 잘 해야한다.
    
    </details>
4. Redis를 이용한 방법 - Lettuce
    <details>
    <summary>펼치기</summary>
   
    ```java
    // RedisLockRepository.class

    @Component
    public class RedisLockRepository {
    
        private RedisTemplate<String, String> redisTemplate;
    
        public RedisLockRepository(RedisTemplate<String, String> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }
    
        public Boolean lock(Long key) {
            return redisTemplate
                    .opsForValue()
                    .setIfAbsent(generateKey(key), "lock", Duration.ofMillis(3_000));
        }
    
        public Boolean unlock(Long key) {
            return redisTemplate.delete(generateKey(key));
        }
    
        private String generateKey(Long key) {
            return key.toString();
        }
    }
    
    // LettuceLockAccountFacade.class
    @Component
    public class LettuceLockAccountFacade {
    
        ...
    
        public void transfer(Long key, Long amount) throws InterruptedException {
            while (!redisLockRepository.lock(key)) {
                Thread.sleep(100);
            }
    
            try {
                accountService.transfer(key, quantity);
            } finally {
                redisLockRepository.unlock(key);
            }
        }
    } 
    ```
   * setnx 명령어를 활용하여 분산락 구현(spin lock 방식 - Lock을 사용 가능한지 일정시간 이후 계속 가서 물어보는 방식), 구현은 간단하지만 동시에 많은 스레드가 lock 획득 대기 상태라면 레디스에 많은 부하가 가해지기 때문에 재시도 시간을 적절히 설정해야한다. spring data redis를 이용하면 lettuce가 기본 방식이다.

    </details>
5. Redis를 이용한 방법 - Redisson
    <details>
    <summary>펼치기</summary>
   
    ```java
    @Component
    public class RedissonLockAccountFacade {
    
        private RedissonClient redissonClient;
        ...
    
        public void transfer(Long key, Long amount) {
            RLock lock = redissonClient.getLock(key.toString());
    
            try {
                    boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);
                    // 5초동안 기다린다. 
                    if (!available) {
                        System.out.println("Lock 획득 실패");
                        return;
                    }
                    accountService.transfer(key, amount);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }
    ```
    * pub-sub 기반으로 Lock 구현 제공. 채널을 하나 만들고 락을 쓰고 있는 스레드가 락을 쓰려는 쓰레드에게 알려주는 방식, 라이브러리를 추가해줘야 한다. 라이브러리에서 Repository를 제공해주기 때문에 Lettuce와 다르게 따로 구현할 필요가 없지만 라이브러리 사용법을 공부해야한다.

    <details>
#### 최종 사용한 방법
##### Pessimistic Lock 방식
* DB 선택한 이유 (DB vs Redis)
  * DB와 레디스 중 성능은 당연히 레디스가 더 앞서지만, 어느정도 트래픽까지는 mysql로도 충분히 사용할 수 있을 것이라고 판단했다. 추후 트래픽이 더 몰린다면 고려했던 순서는 mysql replication을 통해 master/slave를 나눠볼 것이고, 그 이후에도 성능이 부족하다면 Redis를 통해 변경해볼 수 있겠다는 생각을 했다. 
* Pessimistic Lock 선택한 이유 (Pessimistic vs Optimistic)
  * 자바 동기화 블록은 서버가 1대 이상일 경우 무의미하여 고려하지 않았고, 이체 업무에 있어서는 충분히 순간적으로 동시에 이체하는, 충돌이 발생될 수 있는 상황이 있을 수 있다고 판단하여 비관적 락을 선택하였다.

</details>

  
   
   
