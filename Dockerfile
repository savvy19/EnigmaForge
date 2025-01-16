FROM amazoncorretto:17
WORKDIR /app
# Create the necessary directories inside the container
RUN mkdir -p /D/kom/testu
COPY target/document-migration-encryption-0.0.1-SNAPSHOT.jar /app/document_security_migration.jar
COPY /lib /app/lib
EXPOSE 9090
CMD ["java" , "-jar", "document_security_migration.jar"]
