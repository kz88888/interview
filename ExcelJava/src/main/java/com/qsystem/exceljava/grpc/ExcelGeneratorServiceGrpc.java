package com.qsystem.exceljava.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Excel生成服务
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.62.2)",
    comments = "Source: excel_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ExcelGeneratorServiceGrpc {

  private ExcelGeneratorServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.qsystem.exceljava.ExcelGeneratorService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.qsystem.exceljava.grpc.ExcelGenerationRequest,
      com.qsystem.exceljava.grpc.ExcelGenerationResponse> getGenerateExcelMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GenerateExcel",
      requestType = com.qsystem.exceljava.grpc.ExcelGenerationRequest.class,
      responseType = com.qsystem.exceljava.grpc.ExcelGenerationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.qsystem.exceljava.grpc.ExcelGenerationRequest,
      com.qsystem.exceljava.grpc.ExcelGenerationResponse> getGenerateExcelMethod() {
    io.grpc.MethodDescriptor<com.qsystem.exceljava.grpc.ExcelGenerationRequest, com.qsystem.exceljava.grpc.ExcelGenerationResponse> getGenerateExcelMethod;
    if ((getGenerateExcelMethod = ExcelGeneratorServiceGrpc.getGenerateExcelMethod) == null) {
      synchronized (ExcelGeneratorServiceGrpc.class) {
        if ((getGenerateExcelMethod = ExcelGeneratorServiceGrpc.getGenerateExcelMethod) == null) {
          ExcelGeneratorServiceGrpc.getGenerateExcelMethod = getGenerateExcelMethod =
              io.grpc.MethodDescriptor.<com.qsystem.exceljava.grpc.ExcelGenerationRequest, com.qsystem.exceljava.grpc.ExcelGenerationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GenerateExcel"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.qsystem.exceljava.grpc.ExcelGenerationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.qsystem.exceljava.grpc.ExcelGenerationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ExcelGeneratorServiceMethodDescriptorSupplier("GenerateExcel"))
              .build();
        }
      }
    }
    return getGenerateExcelMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.qsystem.exceljava.grpc.QueryRequest,
      com.qsystem.exceljava.grpc.QueryResponse> getQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Query",
      requestType = com.qsystem.exceljava.grpc.QueryRequest.class,
      responseType = com.qsystem.exceljava.grpc.QueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<com.qsystem.exceljava.grpc.QueryRequest,
      com.qsystem.exceljava.grpc.QueryResponse> getQueryMethod() {
    io.grpc.MethodDescriptor<com.qsystem.exceljava.grpc.QueryRequest, com.qsystem.exceljava.grpc.QueryResponse> getQueryMethod;
    if ((getQueryMethod = ExcelGeneratorServiceGrpc.getQueryMethod) == null) {
      synchronized (ExcelGeneratorServiceGrpc.class) {
        if ((getQueryMethod = ExcelGeneratorServiceGrpc.getQueryMethod) == null) {
          ExcelGeneratorServiceGrpc.getQueryMethod = getQueryMethod =
              io.grpc.MethodDescriptor.<com.qsystem.exceljava.grpc.QueryRequest, com.qsystem.exceljava.grpc.QueryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Query"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.qsystem.exceljava.grpc.QueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.qsystem.exceljava.grpc.QueryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ExcelGeneratorServiceMethodDescriptorSupplier("Query"))
              .build();
        }
      }
    }
    return getQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.qsystem.exceljava.grpc.QueryDataRequest,
      com.qsystem.exceljava.grpc.QueryDataResponse> getQueryDataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "QueryData",
      requestType = com.qsystem.exceljava.grpc.QueryDataRequest.class,
      responseType = com.qsystem.exceljava.grpc.QueryDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.qsystem.exceljava.grpc.QueryDataRequest,
      com.qsystem.exceljava.grpc.QueryDataResponse> getQueryDataMethod() {
    io.grpc.MethodDescriptor<com.qsystem.exceljava.grpc.QueryDataRequest, com.qsystem.exceljava.grpc.QueryDataResponse> getQueryDataMethod;
    if ((getQueryDataMethod = ExcelGeneratorServiceGrpc.getQueryDataMethod) == null) {
      synchronized (ExcelGeneratorServiceGrpc.class) {
        if ((getQueryDataMethod = ExcelGeneratorServiceGrpc.getQueryDataMethod) == null) {
          ExcelGeneratorServiceGrpc.getQueryDataMethod = getQueryDataMethod =
              io.grpc.MethodDescriptor.<com.qsystem.exceljava.grpc.QueryDataRequest, com.qsystem.exceljava.grpc.QueryDataResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryData"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.qsystem.exceljava.grpc.QueryDataRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.qsystem.exceljava.grpc.QueryDataResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ExcelGeneratorServiceMethodDescriptorSupplier("QueryData"))
              .build();
        }
      }
    }
    return getQueryDataMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ExcelGeneratorServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ExcelGeneratorServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ExcelGeneratorServiceStub>() {
        @java.lang.Override
        public ExcelGeneratorServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ExcelGeneratorServiceStub(channel, callOptions);
        }
      };
    return ExcelGeneratorServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ExcelGeneratorServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ExcelGeneratorServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ExcelGeneratorServiceBlockingStub>() {
        @java.lang.Override
        public ExcelGeneratorServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ExcelGeneratorServiceBlockingStub(channel, callOptions);
        }
      };
    return ExcelGeneratorServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ExcelGeneratorServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ExcelGeneratorServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ExcelGeneratorServiceFutureStub>() {
        @java.lang.Override
        public ExcelGeneratorServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ExcelGeneratorServiceFutureStub(channel, callOptions);
        }
      };
    return ExcelGeneratorServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Excel生成服务
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * 将JSON数据转换为Excel文件
     * </pre>
     */
    default void generateExcel(com.qsystem.exceljava.grpc.ExcelGenerationRequest request,
        io.grpc.stub.StreamObserver<com.qsystem.exceljava.grpc.ExcelGenerationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGenerateExcelMethod(), responseObserver);
    }

    /**
     * <pre>
     * 查询数据
     * </pre>
     */
    default void query(com.qsystem.exceljava.grpc.QueryRequest request,
        io.grpc.stub.StreamObserver<com.qsystem.exceljava.grpc.QueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryMethod(), responseObserver);
    }

    /**
     * <pre>
     * 查询具体数据
     * </pre>
     */
    default void queryData(com.qsystem.exceljava.grpc.QueryDataRequest request,
        io.grpc.stub.StreamObserver<com.qsystem.exceljava.grpc.QueryDataResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryDataMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service ExcelGeneratorService.
   * <pre>
   * Excel生成服务
   * </pre>
   */
  public static abstract class ExcelGeneratorServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return ExcelGeneratorServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service ExcelGeneratorService.
   * <pre>
   * Excel生成服务
   * </pre>
   */
  public static final class ExcelGeneratorServiceStub
      extends io.grpc.stub.AbstractAsyncStub<ExcelGeneratorServiceStub> {
    private ExcelGeneratorServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ExcelGeneratorServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ExcelGeneratorServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * 将JSON数据转换为Excel文件
     * </pre>
     */
    public void generateExcel(com.qsystem.exceljava.grpc.ExcelGenerationRequest request,
        io.grpc.stub.StreamObserver<com.qsystem.exceljava.grpc.ExcelGenerationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGenerateExcelMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 查询数据
     * </pre>
     */
    public void query(com.qsystem.exceljava.grpc.QueryRequest request,
        io.grpc.stub.StreamObserver<com.qsystem.exceljava.grpc.QueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getQueryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 查询具体数据
     * </pre>
     */
    public void queryData(com.qsystem.exceljava.grpc.QueryDataRequest request,
        io.grpc.stub.StreamObserver<com.qsystem.exceljava.grpc.QueryDataResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryDataMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service ExcelGeneratorService.
   * <pre>
   * Excel生成服务
   * </pre>
   */
  public static final class ExcelGeneratorServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<ExcelGeneratorServiceBlockingStub> {
    private ExcelGeneratorServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ExcelGeneratorServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ExcelGeneratorServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * 将JSON数据转换为Excel文件
     * </pre>
     */
    public com.qsystem.exceljava.grpc.ExcelGenerationResponse generateExcel(com.qsystem.exceljava.grpc.ExcelGenerationRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGenerateExcelMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 查询数据
     * </pre>
     */
    public java.util.Iterator<com.qsystem.exceljava.grpc.QueryResponse> query(
        com.qsystem.exceljava.grpc.QueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getQueryMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 查询具体数据
     * </pre>
     */
    public com.qsystem.exceljava.grpc.QueryDataResponse queryData(com.qsystem.exceljava.grpc.QueryDataRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getQueryDataMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service ExcelGeneratorService.
   * <pre>
   * Excel生成服务
   * </pre>
   */
  public static final class ExcelGeneratorServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<ExcelGeneratorServiceFutureStub> {
    private ExcelGeneratorServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ExcelGeneratorServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ExcelGeneratorServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * 将JSON数据转换为Excel文件
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.qsystem.exceljava.grpc.ExcelGenerationResponse> generateExcel(
        com.qsystem.exceljava.grpc.ExcelGenerationRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGenerateExcelMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 查询具体数据
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.qsystem.exceljava.grpc.QueryDataResponse> queryData(
        com.qsystem.exceljava.grpc.QueryDataRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryDataMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GENERATE_EXCEL = 0;
  private static final int METHODID_QUERY = 1;
  private static final int METHODID_QUERY_DATA = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GENERATE_EXCEL:
          serviceImpl.generateExcel((com.qsystem.exceljava.grpc.ExcelGenerationRequest) request,
              (io.grpc.stub.StreamObserver<com.qsystem.exceljava.grpc.ExcelGenerationResponse>) responseObserver);
          break;
        case METHODID_QUERY:
          serviceImpl.query((com.qsystem.exceljava.grpc.QueryRequest) request,
              (io.grpc.stub.StreamObserver<com.qsystem.exceljava.grpc.QueryResponse>) responseObserver);
          break;
        case METHODID_QUERY_DATA:
          serviceImpl.queryData((com.qsystem.exceljava.grpc.QueryDataRequest) request,
              (io.grpc.stub.StreamObserver<com.qsystem.exceljava.grpc.QueryDataResponse>) responseObserver);
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
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGenerateExcelMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.qsystem.exceljava.grpc.ExcelGenerationRequest,
              com.qsystem.exceljava.grpc.ExcelGenerationResponse>(
                service, METHODID_GENERATE_EXCEL)))
        .addMethod(
          getQueryMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              com.qsystem.exceljava.grpc.QueryRequest,
              com.qsystem.exceljava.grpc.QueryResponse>(
                service, METHODID_QUERY)))
        .addMethod(
          getQueryDataMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.qsystem.exceljava.grpc.QueryDataRequest,
              com.qsystem.exceljava.grpc.QueryDataResponse>(
                service, METHODID_QUERY_DATA)))
        .build();
  }

  private static abstract class ExcelGeneratorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ExcelGeneratorServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.qsystem.exceljava.grpc.ExcelServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ExcelGeneratorService");
    }
  }

  private static final class ExcelGeneratorServiceFileDescriptorSupplier
      extends ExcelGeneratorServiceBaseDescriptorSupplier {
    ExcelGeneratorServiceFileDescriptorSupplier() {}
  }

  private static final class ExcelGeneratorServiceMethodDescriptorSupplier
      extends ExcelGeneratorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    ExcelGeneratorServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (ExcelGeneratorServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ExcelGeneratorServiceFileDescriptorSupplier())
              .addMethod(getGenerateExcelMethod())
              .addMethod(getQueryMethod())
              .addMethod(getQueryDataMethod())
              .build();
        }
      }
    }
    return result;
  }
}
