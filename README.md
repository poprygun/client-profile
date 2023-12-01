# [Client Profile](http://client-profile.default.supply-chain.h2o-4-1333.h2o.vmware.com/)

Client specific configuration for portfolio analysis

## ToDos

- Expand model data with best practices similar to [AWS](https://docs.aws.amazon.com/wellarchitected/latest/analytics-lens/design-principle-12.html)

## Configure workload to access private Gitlab repository

```bash
kp secret create git-credentials \
--git-url https://gitlab.eng.vmware.com \
--git-user ashumilov@vmware.com
git password:
<API TOKEN FROM GITLAB>
```

## Create the app

## Create the app and use Service Bindings

- Spring Boot Buildpack conributes org.springframework.cloud:spring-cloud-bindings:1.10.0
- Spring Cloud Bindings pickup Servivce Binding for Postgres and injects datasource variables
- Application must be deployed in exploded form for buildpack to take effect

`build.gradle`
```groovy
jar {
	enabled = false
}
```

Adding service-ref instructs servide bindings to use posgres instance provisioned in postgres-databases namespace

```bash
tanzu apps workload create client-profile \
--git-repo https://gitlab.eng.vmware.com/vmware-navigator-practice/tooling/client-profile.git \
--type web --git-branch main --namespace default \
--label "app.kubernetes.io/part-of=client-profile" \
--build-env "BP_JVM_VERSION=19" \
--annotation "autoscaling.knative.dev/minScale=1" \
--env "JAVA_TOOL_OPTIONS=-Dmanagement.server.port=8080" \
--service-ref "db=services.apps.tanzu.vmware.com/v1alpha1:ClassClaim:psql-1"
```

## Tail the workload creation

```bash
kp build logs client-profile
tanzu apps workload tail client-profile
```

## Study created workload

```bash
tanzu apps workload get client-profile
```
