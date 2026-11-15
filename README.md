# MY CAR PET

**Drive Together, Grow Together.**

자동차와 함께 이동하며 펫을 키우는 게임의 개발 데모입니다. 기본 캐릭터는 **르르르**, 기본 공간은 **현대자동차 양재사옥**입니다. 최종 목표 플랫폼은 Pleos Connect 차량 내장 화면입니다.

## 다운로드

- [실행용 데모 APK](MY-CAR-PET-demo.apk)
- [Android Studio 프로젝트 전체 소스 ZIP](MyCarPet-AndroidStudio.zip)
- [설명용 화면 재현 이미지와 원본 자산](MyCarPet-preview-images.zip)

이번에 올린 것은 실제로 일반 Android 에뮬레이터에서 실행 화면을 확인한 **원본 보존 버전**입니다. 별도로 개발한 차량 거리→경험치 연결 수정본은 포함하지 않았습니다.

## 빠르게 시연하기

### Android Studio

1. `MyCarPet-AndroidStudio.zip`을 내려받아 압축을 풉니다.
2. **File → Open**에서 `settings.gradle.kts`가 들어 있는 안쪽 **MyCarPet** 폴더를 엽니다. 압축을 푼 바깥 폴더가 아닙니다.
3. **Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK**에서 Java **17 또는 21**을 선택합니다. 기본 Java 25는 포함된 Gradle 8.9와 호환되지 않습니다.
4. Android SDK **API 35**가 설치된 환경에서 Gradle 동기화를 완료합니다.
5. 상단 실행 모듈 **app**, 기기 **일반 Android 에뮬레이터**를 선택하고 **Run ▶**을 누릅니다.

확인한 실행 환경은 **Medium Phone API 37.0**입니다. Windows에서는 에뮬레이터 가속이 필요합니다. Windows 하이퍼바이저 플랫폼을 켠 경우 적용을 위해 재시작이 필요할 수 있습니다.

### APK로 보기

`MY-CAR-PET-demo.apk`는 `app` 모듈의 개발용 디버그 APK입니다. 일반 Android 에뮬레이터에 APK를 드래그해 설치할 수 있습니다. 이는 스토어 배포용 또는 실차 인증 완료 APK가 아닙니다.

## 직접 확인한 결과

2026-11-15 기준:

| 확인 항목 | 결과 |
|---|---|
| app / automotive 원본 소스 빌드 | 성공 |
| Android Studio에서 app 빌드·설치 | 성공 |
| 일반 Android 에뮬레이터에서 게임 화면 표시 | 성공 |
| 르르르·양재사옥 배경·왼쪽 메뉴·오른쪽 상태 패널 표시 | 확인 |
| 모든 메뉴의 동작 및 장시간 안정성 | 미검증 |
| 이번 PC에서 automotive의 Pleos SDK 값 수신·값 변화 | 미검증 |
| 실제 차량 주행·실차 배포 | 미검증 |

## 시연할 수 있는 코드 경로

아래는 현재 코드에 있는 데모 메뉴입니다. 모든 경로를 이번 실행에서 테스트한 것은 아닙니다.

- 기본 화면: 르르르와 양재사옥 배경, 레벨·경험치·코인·테스트 멤버십, 행복도·연료·최근 모의 운행 요약.
- **설정 → 차량 시뮬레이터**: 모의 운행, 연료·배터리 부족 및 주유·충전 상태를 선택하는 데모.
- **설정 → 배고픔 기준**: 기본 20%, 10/20/30% 데모 설정.
- **대화하기**: 서버 주소가 비어 있어 기본 **모의 텍스트 대화**. 음성 AI 대화가 아닙니다.
- **꾸미기**: 스카프 착용 상태 데모.
- **MyHyundai**: 테스트 회원 상태에서 물음표 상자 5개 중 1개를 선택·보관하는 데모. 실제 회원 인증·제휴 혜택 지급이 아닙니다.

행복도·연료·운행 습관·여행지·코인은 현재 메인 게임에서는 **앱 내부 모의 입력**입니다. 실차 센서나 실제 위치에 근거한 운영 기록으로 설명하지 않습니다. 주행 시뮬레이션 중에는 조작을 잠그고 게임 화면 대신 안내를 표시합니다.

## 모듈과 저장

- `app`: 기존 게임 UI와 모바일 GPS 베타. 시작 화면은 `Garage`입니다.
- `automotive`: `ai.pleos.playground:Vehicle:2.3.2`를 사용하는 주행거리 진단 앱. 시작 화면은 `MileageActivity`입니다. Pleos 환경에서만 해당 SDK 서비스를 기대할 수 있습니다.
- `automotive`는 누적 주행거리를 약 5초 간격으로 읽어 표시하는 단계이며, 이 버전에서는 그 값과 게임 경험치가 연결돼 있지 않습니다.
- 이미지 로딩 경로는 `app/src/main/assets/images/`입니다.
- 성장·착용·방문 등은 이 기기의 로컬 저장입니다. 실제 myHyundai 로그인, 계정 복원, 기기 동기화, 차량 간 펫 승계는 미구현입니다.
- 거리 환산과 레벨 등 수치는 코드의 데모 설정이며 최종 운영값으로 확정한 것이 아닙니다.

## 이미지 안내

`MyCarPet-preview-images.zip` 안의 기본 대기·주행 중 잠금·연료 부족 화면은 **현재 코드를 참고한 설명용 재현 이미지**입니다. 실제 에뮬레이터나 실차 캡처가 아니며 이미지 하단에도 표시돼 있습니다. 르르르와 양재사옥 원본 자산도 함께 포함되어 있습니다.

실행·채점은 APK 또는 프로젝트 소스로 확인해 주세요.

## 공식 자료

- [Pleos Playground](https://developers.pleos.ai/playground?locale=ko-KR)
- [Pleos Connect 시작하기](https://document.pleos.ai/docs/pleos-only/getting-started)

소스의 `server/chat.py`는 후속 외부 AI 연동용 개발 서버 예제입니다. 기본 데모 실행에는 필요하지 않으며 실제 비밀 키는 포함하지 않습니다.
