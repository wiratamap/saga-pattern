cd ./service-account
mvn clean test
cd ../
cd ./service-wallet
mvn clean test
cd ../
cd ./service-dlq-platform
mvn clean test
cd ../
cd ./library-kafka-producer
mvn clean test