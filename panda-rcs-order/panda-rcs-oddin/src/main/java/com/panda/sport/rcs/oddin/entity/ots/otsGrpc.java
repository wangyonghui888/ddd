package com.panda.sport.rcs.oddin.entity.ots;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import com.panda.sport.rcs.oddin.entity.ots.TicketOuterClass;
import com.panda.sport.rcs.oddin.entity.ots.ResolveForeignEventsInTicket;
import com.panda.sport.rcs.oddin.entity.ots.TicketCancel;
import com.panda.sport.rcs.oddin.entity.ots.PlayerRiskScore;
import com.panda.sport.rcs.oddin.entity.ots.TicketAck;
import com.panda.sport.rcs.oddin.entity.ots.TicketResultOuterClass;
import com.panda.sport.rcs.oddin.entity.ots.TicketMaxStake;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.53.0)",
    comments = "Source: ots/service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class otsGrpc {

  private otsGrpc() {}

  public static final String SERVICE_NAME = "ots.ots";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<TicketOuterClass.TicketRequest,
      TicketOuterClass.TicketResponse> getTicketMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Ticket",
      requestType = TicketOuterClass.TicketRequest.class,
      responseType = TicketOuterClass.TicketResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<TicketOuterClass.TicketRequest,
      TicketOuterClass.TicketResponse> getTicketMethod() {
    io.grpc.MethodDescriptor<TicketOuterClass.TicketRequest, TicketOuterClass.TicketResponse> getTicketMethod;
    if ((getTicketMethod = otsGrpc.getTicketMethod) == null) {
      synchronized (otsGrpc.class) {
        if ((getTicketMethod = otsGrpc.getTicketMethod) == null) {
          otsGrpc.getTicketMethod = getTicketMethod =
              io.grpc.MethodDescriptor.<TicketOuterClass.TicketRequest, TicketOuterClass.TicketResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Ticket"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  TicketOuterClass.TicketRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  TicketOuterClass.TicketResponse.getDefaultInstance()))
              .setSchemaDescriptor(new otsMethodDescriptorSupplier("Ticket"))
              .build();
        }
      }
    }
    return getTicketMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ResolveForeignEventsInTicket.ResolveForeignEventsInTicketRequest,
      ResolveForeignEventsInTicket.ResolveForeignEventsInTicketResponse> getResolveForeignMatchesInTicketMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ResolveForeignMatchesInTicket",
      requestType = ResolveForeignEventsInTicket.ResolveForeignEventsInTicketRequest.class,
      responseType = ResolveForeignEventsInTicket.ResolveForeignEventsInTicketResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ResolveForeignEventsInTicket.ResolveForeignEventsInTicketRequest,
      ResolveForeignEventsInTicket.ResolveForeignEventsInTicketResponse> getResolveForeignMatchesInTicketMethod() {
    io.grpc.MethodDescriptor<ResolveForeignEventsInTicket.ResolveForeignEventsInTicketRequest, ResolveForeignEventsInTicket.ResolveForeignEventsInTicketResponse> getResolveForeignMatchesInTicketMethod;
    if ((getResolveForeignMatchesInTicketMethod = otsGrpc.getResolveForeignMatchesInTicketMethod) == null) {
      synchronized (otsGrpc.class) {
        if ((getResolveForeignMatchesInTicketMethod = otsGrpc.getResolveForeignMatchesInTicketMethod) == null) {
          otsGrpc.getResolveForeignMatchesInTicketMethod = getResolveForeignMatchesInTicketMethod =
              io.grpc.MethodDescriptor.<ResolveForeignEventsInTicket.ResolveForeignEventsInTicketRequest, ResolveForeignEventsInTicket.ResolveForeignEventsInTicketResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ResolveForeignMatchesInTicket"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ResolveForeignEventsInTicket.ResolveForeignEventsInTicketRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ResolveForeignEventsInTicket.ResolveForeignEventsInTicketResponse.getDefaultInstance()))
              .setSchemaDescriptor(new otsMethodDescriptorSupplier("ResolveForeignMatchesInTicket"))
              .build();
        }
      }
    }
    return getResolveForeignMatchesInTicketMethod;
  }

  private static volatile io.grpc.MethodDescriptor<TicketCancel.TicketCancelRequest,
      TicketCancel.TicketCancelResponse> getCancelTicketMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CancelTicket",
      requestType = TicketCancel.TicketCancelRequest.class,
      responseType = TicketCancel.TicketCancelResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<TicketCancel.TicketCancelRequest,
      TicketCancel.TicketCancelResponse> getCancelTicketMethod() {
    io.grpc.MethodDescriptor<TicketCancel.TicketCancelRequest, TicketCancel.TicketCancelResponse> getCancelTicketMethod;
    if ((getCancelTicketMethod = otsGrpc.getCancelTicketMethod) == null) {
      synchronized (otsGrpc.class) {
        if ((getCancelTicketMethod = otsGrpc.getCancelTicketMethod) == null) {
          otsGrpc.getCancelTicketMethod = getCancelTicketMethod =
              io.grpc.MethodDescriptor.<TicketCancel.TicketCancelRequest, TicketCancel.TicketCancelResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CancelTicket"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  TicketCancel.TicketCancelRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  TicketCancel.TicketCancelResponse.getDefaultInstance()))
              .setSchemaDescriptor(new otsMethodDescriptorSupplier("CancelTicket"))
              .build();
        }
      }
    }
    return getCancelTicketMethod;
  }

  private static volatile io.grpc.MethodDescriptor<PlayerRiskScore.PlayerRiskScoreRequest,
      PlayerRiskScore.PlayerRiskScoreResponse> getPlayerRiskScoreMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PlayerRiskScore",
      requestType = PlayerRiskScore.PlayerRiskScoreRequest.class,
      responseType = PlayerRiskScore.PlayerRiskScoreResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<PlayerRiskScore.PlayerRiskScoreRequest,
      PlayerRiskScore.PlayerRiskScoreResponse> getPlayerRiskScoreMethod() {
    io.grpc.MethodDescriptor<PlayerRiskScore.PlayerRiskScoreRequest, PlayerRiskScore.PlayerRiskScoreResponse> getPlayerRiskScoreMethod;
    if ((getPlayerRiskScoreMethod = otsGrpc.getPlayerRiskScoreMethod) == null) {
      synchronized (otsGrpc.class) {
        if ((getPlayerRiskScoreMethod = otsGrpc.getPlayerRiskScoreMethod) == null) {
          otsGrpc.getPlayerRiskScoreMethod = getPlayerRiskScoreMethod =
              io.grpc.MethodDescriptor.<PlayerRiskScore.PlayerRiskScoreRequest, PlayerRiskScore.PlayerRiskScoreResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PlayerRiskScore"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  PlayerRiskScore.PlayerRiskScoreRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  PlayerRiskScore.PlayerRiskScoreResponse.getDefaultInstance()))
              .setSchemaDescriptor(new otsMethodDescriptorSupplier("PlayerRiskScore"))
              .build();
        }
      }
    }
    return getPlayerRiskScoreMethod;
  }

  private static volatile io.grpc.MethodDescriptor<TicketAck.TicketAckRequest,
      TicketAck.TicketAckResponse> getTicketAckMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TicketAck",
      requestType = TicketAck.TicketAckRequest.class,
      responseType = TicketAck.TicketAckResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<TicketAck.TicketAckRequest,
      TicketAck.TicketAckResponse> getTicketAckMethod() {
    io.grpc.MethodDescriptor<TicketAck.TicketAckRequest, TicketAck.TicketAckResponse> getTicketAckMethod;
    if ((getTicketAckMethod = otsGrpc.getTicketAckMethod) == null) {
      synchronized (otsGrpc.class) {
        if ((getTicketAckMethod = otsGrpc.getTicketAckMethod) == null) {
          otsGrpc.getTicketAckMethod = getTicketAckMethod =
              io.grpc.MethodDescriptor.<TicketAck.TicketAckRequest, TicketAck.TicketAckResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "TicketAck"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  TicketAck.TicketAckRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  TicketAck.TicketAckResponse.getDefaultInstance()))
              .setSchemaDescriptor(new otsMethodDescriptorSupplier("TicketAck"))
              .build();
        }
      }
    }
    return getTicketAckMethod;
  }

  private static volatile io.grpc.MethodDescriptor<TicketResultOuterClass.TicketResultRequest,
      TicketResultOuterClass.TicketResultResponse> getTicketResultMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TicketResult",
      requestType = TicketResultOuterClass.TicketResultRequest.class,
      responseType = TicketResultOuterClass.TicketResultResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<TicketResultOuterClass.TicketResultRequest,
      TicketResultOuterClass.TicketResultResponse> getTicketResultMethod() {
    io.grpc.MethodDescriptor<TicketResultOuterClass.TicketResultRequest, TicketResultOuterClass.TicketResultResponse> getTicketResultMethod;
    if ((getTicketResultMethod = otsGrpc.getTicketResultMethod) == null) {
      synchronized (otsGrpc.class) {
        if ((getTicketResultMethod = otsGrpc.getTicketResultMethod) == null) {
          otsGrpc.getTicketResultMethod = getTicketResultMethod =
              io.grpc.MethodDescriptor.<TicketResultOuterClass.TicketResultRequest, TicketResultOuterClass.TicketResultResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "TicketResult"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  TicketResultOuterClass.TicketResultRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  TicketResultOuterClass.TicketResultResponse.getDefaultInstance()))
              .setSchemaDescriptor(new otsMethodDescriptorSupplier("TicketResult"))
              .build();
        }
      }
    }
    return getTicketResultMethod;
  }

  private static volatile io.grpc.MethodDescriptor<TicketMaxStake.TicketMaxStakeRequest,
      TicketMaxStake.TicketMaxStakeResponse> getTicketMaxStakeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TicketMaxStake",
      requestType = TicketMaxStake.TicketMaxStakeRequest.class,
      responseType = TicketMaxStake.TicketMaxStakeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<TicketMaxStake.TicketMaxStakeRequest,
      TicketMaxStake.TicketMaxStakeResponse> getTicketMaxStakeMethod() {
    io.grpc.MethodDescriptor<TicketMaxStake.TicketMaxStakeRequest, TicketMaxStake.TicketMaxStakeResponse> getTicketMaxStakeMethod;
    if ((getTicketMaxStakeMethod = otsGrpc.getTicketMaxStakeMethod) == null) {
      synchronized (otsGrpc.class) {
        if ((getTicketMaxStakeMethod = otsGrpc.getTicketMaxStakeMethod) == null) {
          otsGrpc.getTicketMaxStakeMethod = getTicketMaxStakeMethod =
              io.grpc.MethodDescriptor.<TicketMaxStake.TicketMaxStakeRequest, TicketMaxStake.TicketMaxStakeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "TicketMaxStake"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  TicketMaxStake.TicketMaxStakeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  TicketMaxStake.TicketMaxStakeResponse.getDefaultInstance()))
              .setSchemaDescriptor(new otsMethodDescriptorSupplier("TicketMaxStake"))
              .build();
        }
      }
    }
    return getTicketMaxStakeMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static otsStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<otsStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<otsStub>() {
        @java.lang.Override
        public otsStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new otsStub(channel, callOptions);
        }
      };
    return otsStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static otsBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<otsBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<otsBlockingStub>() {
        @java.lang.Override
        public otsBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new otsBlockingStub(channel, callOptions);
        }
      };
    return otsBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static otsFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<otsFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<otsFutureStub>() {
        @java.lang.Override
        public otsFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new otsFutureStub(channel, callOptions);
        }
      };
    return otsFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class otsImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<TicketOuterClass.TicketRequest> ticket(
        io.grpc.stub.StreamObserver<TicketOuterClass.TicketResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getTicketMethod(), responseObserver);
    }

    /**
     */
    public void resolveForeignMatchesInTicket(ResolveForeignEventsInTicket.ResolveForeignEventsInTicketRequest request,
        io.grpc.stub.StreamObserver<ResolveForeignEventsInTicket.ResolveForeignEventsInTicketResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getResolveForeignMatchesInTicketMethod(), responseObserver);
    }

    /**
     */
    public void cancelTicket(TicketCancel.TicketCancelRequest request,
        io.grpc.stub.StreamObserver<TicketCancel.TicketCancelResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCancelTicketMethod(), responseObserver);
    }

    /**
     */
    public void playerRiskScore(PlayerRiskScore.PlayerRiskScoreRequest request,
        io.grpc.stub.StreamObserver<PlayerRiskScore.PlayerRiskScoreResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPlayerRiskScoreMethod(), responseObserver);
    }

    /**
     */
    public void ticketAck(TicketAck.TicketAckRequest request,
        io.grpc.stub.StreamObserver<TicketAck.TicketAckResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getTicketAckMethod(), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<TicketResultOuterClass.TicketResultRequest> ticketResult(
        io.grpc.stub.StreamObserver<TicketResultOuterClass.TicketResultResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getTicketResultMethod(), responseObserver);
    }

    /**
     */
    @java.lang.Deprecated
    public void ticketMaxStake(TicketMaxStake.TicketMaxStakeRequest request,
        io.grpc.stub.StreamObserver<TicketMaxStake.TicketMaxStakeResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getTicketMaxStakeMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getTicketMethod(),
            io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
              new MethodHandlers<
                TicketOuterClass.TicketRequest,
                TicketOuterClass.TicketResponse>(
                  this, METHODID_TICKET)))
          .addMethod(
            getResolveForeignMatchesInTicketMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                ResolveForeignEventsInTicket.ResolveForeignEventsInTicketRequest,
                ResolveForeignEventsInTicket.ResolveForeignEventsInTicketResponse>(
                  this, METHODID_RESOLVE_FOREIGN_MATCHES_IN_TICKET)))
          .addMethod(
            getCancelTicketMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                TicketCancel.TicketCancelRequest,
                TicketCancel.TicketCancelResponse>(
                  this, METHODID_CANCEL_TICKET)))
          .addMethod(
            getPlayerRiskScoreMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                PlayerRiskScore.PlayerRiskScoreRequest,
                PlayerRiskScore.PlayerRiskScoreResponse>(
                  this, METHODID_PLAYER_RISK_SCORE)))
          .addMethod(
            getTicketAckMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                TicketAck.TicketAckRequest,
                TicketAck.TicketAckResponse>(
                  this, METHODID_TICKET_ACK)))
          .addMethod(
            getTicketResultMethod(),
            io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
              new MethodHandlers<
                TicketResultOuterClass.TicketResultRequest,
                TicketResultOuterClass.TicketResultResponse>(
                  this, METHODID_TICKET_RESULT)))
          .addMethod(
            getTicketMaxStakeMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                TicketMaxStake.TicketMaxStakeRequest,
                TicketMaxStake.TicketMaxStakeResponse>(
                  this, METHODID_TICKET_MAX_STAKE)))
          .build();
    }
  }

  /**
   */
  public static final class otsStub extends io.grpc.stub.AbstractAsyncStub<otsStub> {
    private otsStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected otsStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new otsStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<TicketOuterClass.TicketRequest> ticket(
        io.grpc.stub.StreamObserver<TicketOuterClass.TicketResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getTicketMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public void resolveForeignMatchesInTicket(ResolveForeignEventsInTicket.ResolveForeignEventsInTicketRequest request,
        io.grpc.stub.StreamObserver<ResolveForeignEventsInTicket.ResolveForeignEventsInTicketResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getResolveForeignMatchesInTicketMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void cancelTicket(TicketCancel.TicketCancelRequest request,
        io.grpc.stub.StreamObserver<TicketCancel.TicketCancelResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCancelTicketMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void playerRiskScore(PlayerRiskScore.PlayerRiskScoreRequest request,
        io.grpc.stub.StreamObserver<PlayerRiskScore.PlayerRiskScoreResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPlayerRiskScoreMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ticketAck(TicketAck.TicketAckRequest request,
        io.grpc.stub.StreamObserver<TicketAck.TicketAckResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getTicketAckMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<TicketResultOuterClass.TicketResultRequest> ticketResult(
        io.grpc.stub.StreamObserver<TicketResultOuterClass.TicketResultResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getTicketResultMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    @java.lang.Deprecated
    public void ticketMaxStake(TicketMaxStake.TicketMaxStakeRequest request,
        io.grpc.stub.StreamObserver<TicketMaxStake.TicketMaxStakeResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getTicketMaxStakeMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class otsBlockingStub extends io.grpc.stub.AbstractBlockingStub<otsBlockingStub> {
    private otsBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected otsBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new otsBlockingStub(channel, callOptions);
    }

    /**
     */
    public ResolveForeignEventsInTicket.ResolveForeignEventsInTicketResponse resolveForeignMatchesInTicket(ResolveForeignEventsInTicket.ResolveForeignEventsInTicketRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getResolveForeignMatchesInTicketMethod(), getCallOptions(), request);
    }

    /**
     */
    public TicketCancel.TicketCancelResponse cancelTicket(TicketCancel.TicketCancelRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelTicketMethod(), getCallOptions(), request);
    }

    /**
     */
    public PlayerRiskScore.PlayerRiskScoreResponse playerRiskScore(PlayerRiskScore.PlayerRiskScoreRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPlayerRiskScoreMethod(), getCallOptions(), request);
    }

    /**
     */
    public TicketAck.TicketAckResponse ticketAck(TicketAck.TicketAckRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getTicketAckMethod(), getCallOptions(), request);
    }

    /**
     */
    @java.lang.Deprecated
    public TicketMaxStake.TicketMaxStakeResponse ticketMaxStake(TicketMaxStake.TicketMaxStakeRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getTicketMaxStakeMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class otsFutureStub extends io.grpc.stub.AbstractFutureStub<otsFutureStub> {
    private otsFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected otsFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new otsFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ResolveForeignEventsInTicket.ResolveForeignEventsInTicketResponse> resolveForeignMatchesInTicket(
        ResolveForeignEventsInTicket.ResolveForeignEventsInTicketRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getResolveForeignMatchesInTicketMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<TicketCancel.TicketCancelResponse> cancelTicket(
        TicketCancel.TicketCancelRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCancelTicketMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<PlayerRiskScore.PlayerRiskScoreResponse> playerRiskScore(
        PlayerRiskScore.PlayerRiskScoreRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPlayerRiskScoreMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<TicketAck.TicketAckResponse> ticketAck(
        TicketAck.TicketAckRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getTicketAckMethod(), getCallOptions()), request);
    }

    /**
     */
    @java.lang.Deprecated
    public com.google.common.util.concurrent.ListenableFuture<TicketMaxStake.TicketMaxStakeResponse> ticketMaxStake(
        TicketMaxStake.TicketMaxStakeRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getTicketMaxStakeMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_RESOLVE_FOREIGN_MATCHES_IN_TICKET = 0;
  private static final int METHODID_CANCEL_TICKET = 1;
  private static final int METHODID_PLAYER_RISK_SCORE = 2;
  private static final int METHODID_TICKET_ACK = 3;
  private static final int METHODID_TICKET_MAX_STAKE = 4;
  private static final int METHODID_TICKET = 5;
  private static final int METHODID_TICKET_RESULT = 6;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final otsImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(otsImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_RESOLVE_FOREIGN_MATCHES_IN_TICKET:
          serviceImpl.resolveForeignMatchesInTicket((ResolveForeignEventsInTicket.ResolveForeignEventsInTicketRequest) request,
              (io.grpc.stub.StreamObserver<ResolveForeignEventsInTicket.ResolveForeignEventsInTicketResponse>) responseObserver);
          break;
        case METHODID_CANCEL_TICKET:
          serviceImpl.cancelTicket((TicketCancel.TicketCancelRequest) request,
              (io.grpc.stub.StreamObserver<TicketCancel.TicketCancelResponse>) responseObserver);
          break;
        case METHODID_PLAYER_RISK_SCORE:
          serviceImpl.playerRiskScore((PlayerRiskScore.PlayerRiskScoreRequest) request,
              (io.grpc.stub.StreamObserver<PlayerRiskScore.PlayerRiskScoreResponse>) responseObserver);
          break;
        case METHODID_TICKET_ACK:
          serviceImpl.ticketAck((TicketAck.TicketAckRequest) request,
              (io.grpc.stub.StreamObserver<TicketAck.TicketAckResponse>) responseObserver);
          break;
        case METHODID_TICKET_MAX_STAKE:
          serviceImpl.ticketMaxStake((TicketMaxStake.TicketMaxStakeRequest) request,
              (io.grpc.stub.StreamObserver<TicketMaxStake.TicketMaxStakeResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_TICKET:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.ticket(
              (io.grpc.stub.StreamObserver<TicketOuterClass.TicketResponse>) responseObserver);
        case METHODID_TICKET_RESULT:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.ticketResult(
              (io.grpc.stub.StreamObserver<TicketResultOuterClass.TicketResultResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class otsBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    otsBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Service.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ots");
    }
  }

  private static final class otsFileDescriptorSupplier
      extends otsBaseDescriptorSupplier {
    otsFileDescriptorSupplier() {}
  }

  private static final class otsMethodDescriptorSupplier
      extends otsBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    otsMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (otsGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new otsFileDescriptorSupplier())
              .addMethod(getTicketMethod())
              .addMethod(getResolveForeignMatchesInTicketMethod())
              .addMethod(getCancelTicketMethod())
              .addMethod(getPlayerRiskScoreMethod())
              .addMethod(getTicketAckMethod())
              .addMethod(getTicketResultMethod())
              .addMethod(getTicketMaxStakeMethod())
              .build();
        }
      }
    }
    return result;
  }
}
