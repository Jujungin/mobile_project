# mobile_project

1. INTRODUCTION
프로젝트에서 구현할 어플리케이션은 이미지 파일을 스캔하여 Text로 추출하는 것이다.
구현하는데 중요한 점은
문자를 어떻게 인식하고 처리하는지가 중요하다.
BitMap을 사용하여 이미지를 저장하고, URI를 사용하여 파일의 경로와 이름을 추출한다.
2. ARCHIVE
프로젝트를 수행하면서 문제가 생겼다
기기마다 경로가 다르고 apk로 만들지 않는 이상 컴퓨터로 디버깅한 경로와 휴대폰의 실제
경로가 다르다는 것이다.
실제 휴대폰의 경로는 “/storage/DCIM/Camera/파일이름.확장자“인 반면
컴퓨터의 시뮬레이터의 경로는 “/storage/emulate/DCIM/0/Camera/파일이름.확장자”이기
때문에 카메라의 기능은 구현하지 못하였다
3. FEATURES
외부 Library중에 tess-two와 openCV가 있는데 openCV로 구현하던중 압축 용량이 30mb를 초
과하여 tess-two 라이브러리를 사용하였다.
라이브러리를 적용하기 위해선 module로 등록을 해줘야 하며 setting.gradle에 라이브러리
프로젝트 이름을 기재하여 동기화를 실행해야한다.
또한 적용할 라이브러리의 build.gradle 폴더와 app의 build.gradle 폴도의 compile, min,
target version을 통일해야 적용이 가능하다
프로젝트에서 사용한 tess-two라이브러리는 .traineddata를 불러와 적용시키는 것이다.
traineddata의 구조는 문자부분과 배경부분의 pixel을 분리하여 문자부분을 추출한되
문자부분의 pixel을 traineddata의 문자와 비교하여 string을 추출하는 것이다.