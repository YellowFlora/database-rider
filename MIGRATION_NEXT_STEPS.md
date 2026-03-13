# Database Rider 2.0 Follow-Up Migration Plan

This document captures the work intentionally deferred after the first 2.0 modernization tranche.

## Current Cut

The current branch delivers:

- Java 17 as the CI and active modernization baseline
- refreshed build and dependency management in the parent reactor
- Spring Framework 6 / Spring Boot 3 migration for the main Spring-facing path
- Kotlin sample migration to Kotlin 2.3 and Jakarta persistence
- Jakarta verification path passing with:
  - `mvn -q -Pjakarta -Dmaven.repo.local=/tmp/database-rider-m2 verify -Pcoverage`

The remaining work should continue as follow-up stages instead of being folded into the current commit.

## Stage A: Finish the Spring Boot 3 migration

### 1. Restore the disabled Spring Boot sample flow

File:

- `rider-examples/spring-boot-dbunit-sample/src/test/java/com/github/database/rider/springboot/SpringBootDBUnitTest.java`

Current status:

- disabled during the Boot 3 migration
- Boot 3 coverage currently relies on the multiple-datasource and leak-hunter paths

Next work:

- identify whether the failure is caused by test context setup, transaction semantics, or dataset lifecycle ordering
- restore one straightforward `@SpringBootTest` regression path that proves Rider works end-to-end on Boot 3
- keep the test representative of the documented usage, not just an internal workaround

### 2. Revisit Data JPA rollback coverage

File:

- `rider-examples/spring-boot-dbunit-sample/src/test/java/com/github/database/rider/springboot/SpringBootDataJpaRollbackTest.java`

Current status:

- still disabled with a known `@DataJpaTest` and `@ExpectedDataSet` limitation

Next work:

- determine whether this is a framework limitation, a Rider lifecycle bug, or outdated test design
- either make it pass under Boot 3 or explicitly document it as unsupported in 2.0

## Stage B: Modernize the deferred example modules

### 3. Migrate `jpa-productivity-boosters`

Files:

- `rider-examples/jpa-productivity-boosters`

Current status:

- still in the legacy example reactor
- not part of the Jakarta verification path

Next work:

- audit DeltaSpike/CDI/test infrastructure for Java 17 and Jakarta compatibility
- migrate `javax.*` persistence, validation, and CDI imports where feasible
- decide whether this module remains an actively supported 2.0 example or moves into a clearly labeled legacy bucket

### 4. Finish the `jOOQ-DBUnit-flyway-example` modernization

Files:

- `rider-examples/jOOQ-DBUnit-flyway-example`

Current status:

- Flyway API usage was updated enough to keep the code aligned with the new baseline
- the module is still not part of the Jakarta verification path

Next work:

- upgrade the example to a current jOOQ-compatible stack on Java 17
- align JDBC/Testcontainers/Flyway settings with the parent version model
- re-enable verification as part of the modern example path when stable

### 5. Review `dbunit-tomee-appcomposer-sample`

Files:

- `rider-examples/dbunit-tomee-appcomposer-sample`

Current status:

- still treated as a legacy example

Next work:

- determine whether Tomee/AppComposer remains a supported 2.0 example
- if yes, migrate it to the Java 17 toolchain and verify it still demonstrates meaningful coverage
- if not, move it behind an explicit legacy profile or document it as no longer maintained

## Stage C: Quarkus migration

### 6. Upgrade Quarkus examples to Quarkus 3.x

Files:

- `rider-examples/quarkus-dbunit-sample`
- `rider-examples/quarkus-postgres-sample`

Current status:

- still on older Quarkus-era assumptions
- not yet part of the Jakarta modernization path

Next work:

- upgrade to a supported Quarkus 3.x line on Java 17
- migrate imports and configuration to Jakarta APIs
- update test infrastructure and datasource wiring
- re-evaluate disabled coverage in:
  - `rider-examples/quarkus-dbunit-sample/src/test/java/com/github/quarkus/sample/QuarkusMultipleDataSourceTest.java`

Desired exit criteria:

- at least one Quarkus sample passes in the modern verification path
- multiple datasource behavior is either restored or explicitly documented as deferred

## Stage D: Micronaut migration

### 7. Upgrade Micronaut example to Micronaut 4.x

Files:

- `rider-examples/rider-micronaut`
- `rider-junit5/src/main/java/com/github/database/rider/junit5/integration/Micronaut.java`

Current status:

- Micronaut example still contains JCenter-era configuration
- not part of the current Jakarta verification path

Next work:

- remove JCenter from Maven and Gradle build files
- upgrade to Micronaut 4.x on Java 17
- update test extension usage to current Micronaut JUnit 5 APIs
- verify the JUnit 5 Micronaut integration still resolves connections correctly against the upgraded example

## Stage E: Artifact and profile policy cleanup

### 8. Finalize 2.0 artifact policy

Current status:

- the modernization branch behaves as Jakarta-first for the actively maintained framework path
- legacy modules still exist in parallel

Next work:

- define whether 2.0 keeps dual-mode publication in `rider-core` only
- define whether framework-facing integrations are Jakarta-first only
- align README and module docs around one clear artifact/classifier story

### 9. Replace the temporary example-reactor split with a durable profile strategy

File:

- `rider-examples/pom.xml`

Current status:

- `legacy-examples` is active by default
- `jakarta` currently includes only the modernized Spring and Kotlin examples

Next work:

- keep this split while migrations are in flight
- once Quarkus, Micronaut, and retained legacy examples are triaged, replace the transitional setup with a clearer long-term profile model

Possible end state:

- `modern-examples`
- `legacy-examples`
- optional framework-specific profiles if the matrix remains too large for one default verification path

## Stage F: Documentation and release framing

### 10. Expand the 2.0 migration guidance

Files:

- `README.adoc`
- module READMEs
- changelog / release notes

Next work:

- add a full upgrade guide for users moving from 1.x
- document Java 17 as the baseline
- document Spring 6 / Boot 3 expectations
- document Jakarta package migration and classifier usage
- explicitly list deferred example migrations so the staged release shape is clear

## Recommended Execution Order

1. Commit the current modernization tranche.
2. Restore the disabled Spring Boot test coverage.
3. Modernize Quarkus examples.
4. Modernize the Micronaut example and integration edge.
5. Triage and migrate or isolate `jpa-productivity-boosters`, `jOOQ`, and Tomee.
6. Finalize artifact/profile policy.
7. Refresh docs and rerun the full verification strategy for the updated matrix.
