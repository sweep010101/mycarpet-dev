## 1. 앱 이름 및 한 줄 소개

**MY CAR PET — Drive Together, Grow Together.**

자동차와 함께 이동하며 펫을 키우는 육성 게임으로, Pleos Connect 차량 내장 화면에서의 이용을 목표로 합니다. 기본 캐릭터는 르르르, 기본 공간은 현대자동차 양재사옥입니다.

## 2. 실행 방법

1. 이 저장소의 **Code → Download ZIP**을 선택해 소스를 내려받고 압축을 풉니다. 또는 `git clone https://github.com/sweep010101/mycarpet-dev.git`으로 받습니다.
2. Android Studio의 **File → Open**에서 `settings.gradle.kts`가 있는 저장소 루트 폴더를 엽니다.
3. Gradle 동기화를 완료합니다.
4. **게임 데모:** 상단 실행 모듈 `app`, 일반 Android 에뮬레이터를 선택하고 **Run ▶**을 누릅니다.
5. **차량 SDK 진단 앱:** 모듈 `automotive`, Pleos Connect 에뮬레이터를 선택합니다. 이 모듈은 누적 주행거리 표시용으로, 게임 화면·경험치와 아직 연결되어 있지 않습니다.

APK로 시연할 때는 아래 APK를 일반 Android 에뮬레이터에 드래그해 설치합니다.

## 3. 테스트 환경 및 필요한 설정

- **실행 확인:** Windows PC / Android Studio / Medium Phone API 37.0 일반 Android 에뮬레이터에서 `app` 빌드·설치·화면 실행 성공. 르르르, 배경, 왼쪽 메뉴와 오른쪽 상태 패널 표시 확인.
- **빌드:** Android SDK API 35, Gradle 8.9, Android Gradle Plugin 8.7.3, Kotlin 2.0.21. `app`과 `automotive` 빌드 성공.
- **JDK:** 17 또는 21. **Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK**에서 선택합니다. Java 25는 포함된 Gradle과 호환되지 않습니다.
- **Windows 에뮬레이터:** BIOS 가상화와 Windows 하이퍼바이저 플랫폼이 필요하며, Windows 기능 변경 후에는 재시작이 필요할 수 있습니다.
- **데모 데이터:** 게임의 운행·행복도·연료·여행지 데이터는 앱 내부 모의 입력입니다. 대화는 기본 모의 텍스트 대화이며 외부 AI 서버나 API 키 없이 실행합니다. 멤버십도 테스트 상태입니다.
- **검증 범위:** 모든 메뉴의 동작과 장시간 안정성, 이번 PC에서의 Pleos SDK 값 수신·변경, 실차 동작은 미검증입니다. 성장 상태는 기기에 로컬 저장되며 계정 복원·기기 동기화는 미구현입니다.

## 4. 개발에 사용한 Pleos Connect 에뮬레이터 버전과 화면 규격

설치된 개발 대상의 설정값입니다. 일반 Android 게임 데모의 실행 확인 결과와 구분합니다.

| 항목 | 설정 |
|---|---|
| Pleos Connect 시스템 이미지 | **v2.1.0** (`release_connect_v2.1.0`) |
| 기반 OS / API | Android 14 / API 34 |
| CPU 아키텍처 | x86_64 |
| 화면 해상도 / 방향 | **2560 × 1440 px / 가로형(16:9)** |
| 화면 밀도 | 160 dpi |
| 가상기기 RAM | 4 GB |
| 차량 SDK | `ai.pleos.playground:Vehicle:2.3.2` |

이 PC에서 Pleos 차량용 화면 실행 및 주행거리 응답은 아직 검증하지 않았습니다. Pleos Emulator 버전(v2.1.0)과 Vehicle SDK 버전(2.3.2)은 서로 다릅니다.

## 5. APK 파일 위치 또는 GitHub Release 링크

**APK 위치:** 저장소 루트의 [`MY-CAR-PET-demo.apk`](MY-CAR-PET-demo.apk)

- [APK 직접 다운로드](https://github.com/sweep010101/mycarpet-dev/raw/refs/heads/main/MY-CAR-PET-demo.apk)
- 패키지: `kr.mycarpet.demo` / 시작 화면: `Garage`
- `app` 모듈의 개발용 디버그 APK이며, 일반 Android 게임 데모용입니다. 실차 인증 또는 스토어 배포용 APK가 아닙니다.
- 별도 GitHub Release는 발행하지 않았습니다.

참고: [`MyCarPet-preview-images.zip`](MyCarPet-preview-images.zip)은 설명용 화면 재현 이미지와 원본 자산입니다. 실제 에뮬레이터·실차 캡처가 아니며, 실행 여부는 APK 또는 프로젝트 소스로 확인해 주세요.
