package example

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import example.protos._
import fs2.grpc.syntax.all._
import io.grpc._
import io.grpc.netty.NettyServerBuilder

class ExampleImplementation extends GreeterFs2Grpc[IO, Metadata] {
  override def sayHello(request: HelloRequest, clientHeaders: Metadata): IO[HelloReply] =
    IO(HelloReply("Request name is: " + request.name))
}

object Main extends IOApp {
  private val helloService: Resource[IO, ServerServiceDefinition] =
    GreeterFs2Grpc.bindServiceResource[IO](new ExampleImplementation)

  private def runuble(service: ServerServiceDefinition): IO[Unit] =
    NettyServerBuilder
      .forPort(9999)
      .addService(service)
      .resource[IO]
      .evalMap(server => IO(server.start()))
      .useForever
      .void

  override def run(
      args: List[String]
    ): IO[ExitCode] =
    helloService.use(runuble).redeem(_ => ExitCode.Error, _ => ExitCode.Success)
}
