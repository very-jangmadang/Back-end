# Back-end
Back-end 공간입니다.


커밋 규칙

1. main과 release 브랜치는 확인 후에만 커밋이 가능하다. ( free 버전 private라서 잠가놓지는 못했음 )
   
2. 브랜치는 아래와 같이 총 5개이다.
  - main : 배포 가능한 최종 버전. 직접 커밋 금지. ( 실제 서버 )
  - release : 배포 가능한 최종 버전. 직접 커밋 금지. ( 예비 서버 → main 손상 시 사용 )
  - develop : 통합 테스트용 브랜치.
  - feature/ : 기능 개발 브랜치. 각 기능별로 생성해서 사용한다. (ex. `feature/login` )
  - hotfix/ : 긴급 수정 브랜치 (ex. `hotfix/bug-fix` )
    
3. ci 규칙
    - main 또는 release 브랜치로 PR 할 때만 CI가 실행되도록 설정
    
4. cd 규칙
    - main 혹은 release 브랜치로 Push 할 때만 배포가 실행되도록 설정
