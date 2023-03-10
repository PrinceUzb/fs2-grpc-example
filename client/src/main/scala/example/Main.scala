package example

import cats.effect._
import example.protos._
import fs2.grpc.syntax.all._
import io.grpc._
import io.grpc.netty.NettyChannelBuilder

object Main extends IOApp {
  private val managedChannelResource: Resource[IO, ManagedChannel] =
    NettyChannelBuilder
      .forAddress("127.0.0.1", 9999)
      .usePlaintext()
      .resource[IO]

  private def runProgram(helloStub: GreeterFs2Grpc[IO, Metadata]): IO[Unit] =
    for {
      response <- helloStub.sayHello(HelloRequest("John Doe"), new Metadata())
      _ <- IO.println(response.message)
    } yield ()

  override def run(
      args: List[String]
    ): IO[ExitCode] =
    managedChannelResource
      .flatMap(ch => GreeterFs2Grpc.stubResource[IO](ch))
      .use(runProgram)
      .redeem(_ => ExitCode.Error, _ => ExitCode.Success)
}
