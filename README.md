# Goal Management Service

## ディレクトリ構成
DDD & gRPC構成

```
src/
├── main/
│   ├── kotlin/
│   │   └── com/
│   │       └── example/
│   │           └── goal_achieve/
│   │               ├── GoalAchieveApplication.kt
│   │               ├── application/                     # アプリケーション層
│   │               │   └── service/                    
│   │               │       └── GoalApplicationService.kt # ユースケース実装
│   │               ├── domain/                          # ドメイン層
│   │               │   ├── model/                      
│   │               │   │   ├── Goal.kt                  # エンティティ
│   │               │   │   └── GoalStatus.kt            # 値オブジェクト
│   │               │   ├── service/                    
│   │               │   │   └── GoalDomainService.kt     # ドメインサービス
│   │               │   └── repository/                 
│   │               │       └── GoalRepository.kt        # リポジトリインターフェース
│   │               └── infrastructure/                  # インフラストラクチャ層
│   │                   ├── persistence/                
│   │                   │   ├── entity/                 
│   │                   │   │   └── GoalEntity.kt        # DB用エンティティ
│   │                   │   ├── repository/             
│   │                   │   │   └── GoalJpaRepository.kt # Spring Data JPA
│   │                   │   └── GoalRepositoryImpl.kt    # リポジトリ実装
│   │                   └── grpc/                       
│   │                       ├── mapper/                 
│   │                       │   └── GoalMapper.kt        # ドメイン<->gRPC変換
│   │                       └── service/                
│   │                           └── GoalGrpcService.kt   # gRPCサービス実装
│   ├── proto/                                           # Protocol Buffers定義
│   │   └── goal.proto
│   └── resources/
│       └── application.yml
└── test/
    └── kotlin/
        └── com/
            └── example/
                └── goal_achieve/
                    ├── domain/
                    │   └── model/
                    │       └── GoalTest.kt
                    ├── application/
                    │   └── service/
                    │       └── GoalApplicationServiceTest.kt
                    └── infrastructure/
                        ├── persistence/
                        │   └── GoalRepositoryImplTest.kt
                        └── grpc/
                            └── service/
                                └── GoalGrpcServiceTest.kt
```