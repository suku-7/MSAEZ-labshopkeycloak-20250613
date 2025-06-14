# Model
## MSAEZ-labshopkeycloak-20250613 (주문 데이터 실시간 동기화 확인-CDC)

Keycloak 서버와 인증이 필요한 두 개의 서비스(Gateway, Order)를 구동했습니다.
이후 사용자 역할에 따라 JWT 토큰을 발급받아 각 리소스에 대한 접근 권한이 올바르게 작동하는지 테스트하고 검증했습니다.
최종적으로는 사용자 및 관리자 권한별 접근 제어의 성공적인 구현을 확인했습니다.

![스크린샷 2025-06-13 101457](https://github.com/user-attachments/assets/bc5e4ece-bc94-43bf-855b-7be471edc8ef)
![스크린샷 2025-06-13 102008](https://github.com/user-attachments/assets/7f554169-8352-439f-93fd-825bb5169037)
![스크린샷 2025-06-13 103559](https://github.com/user-attachments/assets/4b41e07d-190c-4c34-b9bc-17c9e8d96898)
![스크린샷 2025-06-13 104319](https://github.com/user-attachments/assets/6d317ff9-04de-4ff7-990d-545d192a1fef)
![스크린샷 2025-06-13 105125](https://github.com/user-attachments/assets/da007d79-c02d-4f2a-b74d-e22eff55ba00)
![스크린샷 2025-06-13 105543](https://github.com/user-attachments/assets/1f8a30ea-37e7-4b26-a91b-5d24729da0e4)
![스크린샷 2025-06-13 105807](https://github.com/user-attachments/assets/c1b40dff-64b6-4827-8af4-a05ddd324199)
![스크린샷 2025-06-13 110400](https://github.com/user-attachments/assets/bd9783a6-5e50-4c30-a951-bbf19e3b2238)

---
## 터미널 작성 참고용
Keycloak 기반 JWT 인증 및 권한 부여 테스트  
이 문서는 Keycloak을 활용하여 JWT(JSON Web Token) 기반 인증 및 권한 부여 시스템이 어떻게 동작하는지 테스트하는 과정을 안내합니다.  

1. Java SDK 설치
먼저 Java Development Kit (JDK)를 설치합니다.  
```
sdk install java
```
2. Keycloak 서버 구동
Keycloak 서버를 로컬에서 개발 모드로 실행합니다. 기본적으로 8080 포트를 사용합니다.  
```
cd token-based-auth-Keycloak/keycloak/bin
chmod 744 kc.sh
./kc.sh start-dev
```
3. Gateway 및 Order 서비스 실행
인증 및 권한 부여가 적용될 Gateway (8088 포트)와 Order 서비스 (8081 포트)를 각각 실행합니다.  
```
cd token-based-auth-Keycloak/gateway
mvn spring-boot:run

# 새 터미널에서 실행
cd token-based-auth-Keycloak/order
mvn spring-boot:run
```
4. Protected 리소스 초기 접근 테스트
인증되지 않은 상태에서 보호된 리소스에 접근하여 401 (Unauthorized) 오류를 확인하고, 모두에게 허가된 리소스는 정상 접근되는지 확인합니다.  
```
http http://localhost:8088
http http://localhost:8088/orders
http http://localhost:8088/test/permitAll
```
5. 사용자 토큰 발급
user@uengine.org 계정으로 Keycloak에서 액세스 토큰(JWT)을 발급받습니다. 이 토큰은 이후 보호된 리소스에 접근할 때 사용됩니다.  
```
curl -X POST "http://localhost:8080/realms/my_realm/protocol/openid-connect/token" \
--header "Content-Type: application/x-www-form-urlencoded" \
--data-urlencode "grant_type=password" \
--data-urlencode "client_id=my_client" \
--data-urlencode "client_secret=HKFKYP7kb8OMldAgfvnk27FhRPOv8Y7H" \
--data-urlencode "username=user@uengine.org" \
--data-urlencode "password=1"
```
6. 사용자 토큰을 이용한 리소스 접근 테스트
발급받은 사용자 토큰을 Authorization 헤더에 포함하여 다양한 보호된 리소스에 접근을 시도합니다.
```
export access_token="eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJmZjBoS1Uzb21HUnpfWjB5cDd0eUpWT3N2M2kyYmQ2ZlV3T2R2TWFSQW44In0.eyJleHAiOjE3NDk3ODI1OTMsImlhdCI6MTc0OTc3ODk5MywianRpIjoiYjljMDk4OGYtMGZkZi00OTE0LTkxYjQtMjRiOTc3MzBjZDg1IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9teV9yZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI3ZTAxY2I2OC1jNDMzLTQ1ZmUtYTMzYy04MzAwMzY3YjEzNDQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJteV9jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiZjdmMDRiOWQtNDUyMS00MjU5LTkzYjctNmVmNTQxY2Q4M2E5IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtbXlfcmVhbG0iLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsibXlfY2xpZW50Ijp7InJvbGVzIjpbIlJPTEVfVVNFUiJdfSwiYWNjX291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6IjdmMDRiOWQtNDUyMS00MjU5LTkzYjctNmVmNTQxY2Q4M2E5IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ1c2VyQHVlbmdpbmUub3JnIn0.YPFsfuWjGO64rw-3ShVoZ2kbet7XbggQp3LPvXxn7JiAyXh8LSrYVvTiclWGydDQIGE8Q17JN-vl5joMyVBUQEvvfm76eYQdyrkXNMv9U2YkfhSLerzPnEG5Sl-G165B4JoMdYAYL4DqakkW-c0diCluaaFhk4Lt-NT76kT3fGVnVj7yepNd49X7346x6-sM8EklmlIlLiRttedtbwI0a7CeaQiCE8HUWIDUb5mDzn0SOwgnXKgngUw-PU0GZF8XC10EKEg3siY4GNTfhPZfPlOD44AXccqCuNPoJJ8KQsRlYGpwqVShDdWEW-Z_v4oCN_40M3pWnM2yJ7PYhyNLaA"

echo $access_token
http localhost:8088/orders "Authorization: Bearer $access_token"
http localhost:8088/test/user "Authorization: Bearer $access_token"
http localhost:8088/test/authenticated "Authorization: Bearer $access_token"
http localhost:8088/test/admin "Authorization: Bearer $access_token"
```
테스트 결과 (사용자 토큰):

/orders: 정상 (200 OK)  
/test/user: 정상 (200 OK) - 사용자 역할로 접근 허용  
/test/authenticated: 정상 (200 OK) - 인증된 사용자로서 접근 허용  
/test/admin: 비정상 (403 Forbidden) - 현재 토큰에 admin 권한이 없어 접근 거부  

7. Admin 동작용 토큰 재발급
admin@uengine.org 계정으로 Keycloak에서 액세스 토큰을 재발급받습니다. 이 토큰에는 admin 권한이 포함되어 있습니다.
```
curl -X POST "http://localhost:8080/realms/my_realm/protocol/openid-connect/token" \
--header "Content-Type: application/x-www-form-urlencoded" \
--data-urlencode "grant_type=password" \
--data-urlencode "client_id=my_client" \
--data-urlencode "client_secret=HKFKYP7kb8OMldAgfvnk27FhRPOv8Y7H" \
--data-urlencode "username=admin@uengine.org" \
--data-urlencode "password=1"
```
8. Admin 토큰을 이용한 리소스 접근 테스트
새로 발급받은 admin 토큰으로 /test/admin 리소스에 접근을 시도합니다.

```
export access_token="eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJmZjBoS1Uzb21HUnpfWjB5cDd0eUpWT3N2M2kyYmQ2ZlV3T2R2TWFSQW44In0.eyJleHAiOjE3NDk3ODMzMzgsImlhdCI6MTc0OTc3OTczOCwianRpIjoiMGU0NDFkZTMtYTU3MC00YjA1LTgyNmMtZDI5YTQwZjg1MjY5IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9teV9yZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI0YTM5MTVlMy0xMWNiLTRjNzEtOGFiZC1kMWM3YTI0NDk5ZGQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJteV9jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiODIxZjFkYjMtMWVmZS00MDFmLWJiNTUtZjFmZmFkMzAxZjlhIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtbXlfcmVhbG0iLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsibXlfY2xpZW50Ijp7InJvbGVzIjpbIlJPTEVfVVNFUiIsIlJPTEVfQURNSU4iXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6IjgyMWYxZGIzLTFlZmUtNDAxfi1iYjU1LWYxZmZhZDMwMWY5YSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicHJlZmVycmVkX3VzZXJuYW1lIjoiYWRtaW5AdWVuZ2luZS5vcmcifQ.dIitr-Pq0bx-We72VilfwIp0IO0SfFL5Vxy_RJCjflpvUgjqfmL_uwSR6znJxJIBNeAXWf-8zRLW9vo8SL7qyrUmHMUFST_Htb4oS9X_5ODI6Z0hS1zr_OZdvLRJ-VsgApr3ZM0glcFBoy0FyVxESScS3jtPj40gATJN3NefSv9Hz2Aq6PYV9FfLCLikh9Ml23ZvWkUAvymec37NoqNUtQBUQoEZJyooFyOqoB8Vb42sJzQ_VLRpiombBpo8Dy1m2rxZP7gd5kujpF-IHhKv8PVnLx71ZxeCJZzuhYO5X3f3ImImPsEyp7GSPaRicBZWYr2NhctojiQtZgzDVq6Jrw"

http localhost:8088/test/admin "Authorization: Bearer $access_token"
```
테스트 결과 (Admin 토큰):  
/test/admin: 정상 (200 OK) - 성공적으로 admin 권한을 가진 리소스에 접근  

9. 서비스 종료
다음 작업을 위해 실행 중인 서비스들을 종료합니다.  
```
fuser -k 8080/tcp
fuser -k 8081/tcp
fuser -k 8088/tcp
```
