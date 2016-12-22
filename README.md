##ambry-client-akkahttp


###FUN
Aim of this library is to allow end to end binary file streaming for services using Ambry,  really cool new 
distributed object store technology. The library has hexagonal architecture so its easily extensible. Currently akka-http client 
is implemented. I aim to implemeent different types of client with different thread pool configurations inorder to test and compare 
their performance: uploading and downloading large files.


###OVERVIEW
ambry-client-akkahttp is Akka-http client library for [Ambry](https://github.com/linkedin/ambry) distributed object store in Scala

###QUICK START

Scala: 2.11.8

Akka-Http: 10.0.0

Akka: 2.14

For using Akka-Http Ambry client:

import io.pixelart.ambry.client.application.AmbryAkkaHttpClient

val ambryClient = AmbryAkkaHttpClient(host, port) 

With following implicits in scope: ActorSystem, ExecutionContext, and ActorMaterializer

###DETAILED USAGE

Model usage



###CAVEATS

Not all http interface methods are implemented yet; the implemented method list:
1.HealthCheck
2.UploadFile
3.


###LICENSE
 
