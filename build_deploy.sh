# shellcheck disable=SC1113
#/bin/bash
export module=$1
export version=$(git rev-parse --short HEAD)-$(date +%s)
echo ' Building artifact ' ${module} ${version}
mvn clean install -am
#echo ' Logging in to azure container registry4cbc'
az acr login -n registry4cbc
echo '------------------------------------------------------------------------'
echo 'Building ${module} image with version tag ' ${version}
echo '------------------------------------------------------------------------'
docker buildx build --platform linux/amd64 --tag registry4cbc.azurecr.io/${module}:${version} --tag registry4cbc.azurecr.io/${module}:LATEST .
echo '------------------------------------------------------------------------'
echo 'Pushing ${module} image with version ' ${version} and 'LATEST'
docker push registry4cbc.azurecr.io/${module}:${version}
docker push registry4cbc.azurecr.io/${module}:LATEST
echo '------------------------------------------------------------------------'
echo 'Deploying new version ' ${version} ' to production '
currentVersion=$(kubectl get deployment ${module} -o=jsonpath='{$.spec.template.spec.containers[:1].image}')
echo 'Current container image in production : registry4cbc.azurecr.io/'${module}:$currentVersion
echo 'New container image to be deployed in production : registry4cbc.azurecr.io/'${module}:${version}
kubectl set image deployment/${module} ${module}=registry4cbc.azurecr.io/${module}:${version}
kubectl rollout status -w deployment/${module}
currentVersion=$(kubectl get deployment ${module} -o=jsonpath='{$.spec.template.spec.containers[:1].image}')
echo 'Current version in production :' $currentVersion