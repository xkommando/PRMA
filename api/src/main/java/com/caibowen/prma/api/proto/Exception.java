// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Exception.proto

package com.caibowen.prma.api.proto;

public final class Exception {
  private Exception() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface ExceptionPOOrBuilder extends
      // @@protoc_insertion_point(interface_extends:com.caibowen.prma.api.proto.ExceptionPO)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required uint64 id = 1;</code>
     */
    boolean hasId();
    /**
     * <code>required uint64 id = 1;</code>
     */
    long getId();

    /**
     * <code>required string exception_name = 2;</code>
     */
    boolean hasExceptionName();
    /**
     * <code>required string exception_name = 2;</code>
     */
    String getExceptionName();
    /**
     * <code>required string exception_name = 2;</code>
     */
    com.google.protobuf.ByteString
        getExceptionNameBytes();

    /**
     * <code>optional string exception_msg = 3;</code>
     */
    boolean hasExceptionMsg();
    /**
     * <code>optional string exception_msg = 3;</code>
     */
    String getExceptionMsg();
    /**
     * <code>optional string exception_msg = 3;</code>
     */
    com.google.protobuf.ByteString
        getExceptionMsgBytes();

    /**
     * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
     */
    java.util.List<StackTrace.StackTracePO>
        getStacktracesList();
    /**
     * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
     */
    StackTrace.StackTracePO getStacktraces(int index);
    /**
     * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
     */
    int getStacktracesCount();
    /**
     * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
     */
    java.util.List<? extends StackTrace.StackTracePOOrBuilder>
        getStacktracesOrBuilderList();

      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
    StackTrace.StackTracePOOrBuilder getStacktracesOrBuilder(
            int index);
  }
  /**
   * Protobuf type {@code com.caibowen.prma.api.proto.ExceptionPO}
   */
  public static final class ExceptionPO extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:com.caibowen.prma.api.proto.ExceptionPO)
      ExceptionPOOrBuilder {
    // Use ExceptionPO.newBuilder() to construct.
    private ExceptionPO(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private ExceptionPO(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final ExceptionPO defaultInstance;
    public static ExceptionPO getDefaultInstance() {
      return defaultInstance;
    }

    public ExceptionPO getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private ExceptionPO(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              id_ = input.readUInt64();
              break;
            }
            case 18: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000002;
              exceptionName_ = bs;
              break;
            }
            case 26: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000004;
              exceptionMsg_ = bs;
              break;
            }
            case 34: {
              if (!((mutable_bitField0_ & 0x00000008) == 0x00000008)) {
                stacktraces_ = new java.util.ArrayList<StackTrace.StackTracePO>();
                mutable_bitField0_ |= 0x00000008;
              }
              stacktraces_.add(input.readMessage(StackTrace.StackTracePO.PARSER, extensionRegistry));
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        if (((mutable_bitField0_ & 0x00000008) == 0x00000008)) {
          stacktraces_ = java.util.Collections.unmodifiableList(stacktraces_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return Exception.internal_static_com_caibowen_prma_api_proto_ExceptionPO_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return Exception.internal_static_com_caibowen_prma_api_proto_ExceptionPO_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ExceptionPO.class, Builder.class);
    }

    public static com.google.protobuf.Parser<ExceptionPO> PARSER =
        new com.google.protobuf.AbstractParser<ExceptionPO>() {
      public ExceptionPO parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new ExceptionPO(input, extensionRegistry);
      }
    };

    @Override
    public com.google.protobuf.Parser<ExceptionPO> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    public static final int ID_FIELD_NUMBER = 1;
    private long id_;
    /**
     * <code>required uint64 id = 1;</code>
     */
    public boolean hasId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required uint64 id = 1;</code>
     */
    public long getId() {
      return id_;
    }

    public static final int EXCEPTION_NAME_FIELD_NUMBER = 2;
    private Object exceptionName_;
    /**
     * <code>required string exception_name = 2;</code>
     */
    public boolean hasExceptionName() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required string exception_name = 2;</code>
     */
    public String getExceptionName() {
      Object ref = exceptionName_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          exceptionName_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string exception_name = 2;</code>
     */
    public com.google.protobuf.ByteString
        getExceptionNameBytes() {
      Object ref = exceptionName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        exceptionName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int EXCEPTION_MSG_FIELD_NUMBER = 3;
    private Object exceptionMsg_;
    /**
     * <code>optional string exception_msg = 3;</code>
     */
    public boolean hasExceptionMsg() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>optional string exception_msg = 3;</code>
     */
    public String getExceptionMsg() {
      Object ref = exceptionMsg_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          exceptionMsg_ = s;
        }
        return s;
      }
    }
    /**
     * <code>optional string exception_msg = 3;</code>
     */
    public com.google.protobuf.ByteString
        getExceptionMsgBytes() {
      Object ref = exceptionMsg_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        exceptionMsg_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int STACKTRACES_FIELD_NUMBER = 4;
    private java.util.List<StackTrace.StackTracePO> stacktraces_;
    /**
     * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
     */
    public java.util.List<StackTrace.StackTracePO> getStacktracesList() {
      return stacktraces_;
    }
    /**
     * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
     */
    public java.util.List<? extends StackTrace.StackTracePOOrBuilder>
        getStacktracesOrBuilderList() {
      return stacktraces_;
    }
    /**
     * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
     */
    public int getStacktracesCount() {
      return stacktraces_.size();
    }
    /**
     * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
     */
    public StackTrace.StackTracePO getStacktraces(int index) {
      return stacktraces_.get(index);
    }
    /**
     * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
     */
    public StackTrace.StackTracePOOrBuilder getStacktracesOrBuilder(
        int index) {
      return stacktraces_.get(index);
    }

    private void initFields() {
      id_ = 0L;
      exceptionName_ = "";
      exceptionMsg_ = "";
      stacktraces_ = java.util.Collections.emptyList();
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasId()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasExceptionName()) {
        memoizedIsInitialized = 0;
        return false;
      }
      for (int i = 0; i < getStacktracesCount(); i++) {
        if (!getStacktraces(i).isInitialized()) {
          memoizedIsInitialized = 0;
          return false;
        }
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeUInt64(1, id_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, getExceptionNameBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeBytes(3, getExceptionMsgBytes());
      }
      for (int i = 0; i < stacktraces_.size(); i++) {
        output.writeMessage(4, stacktraces_.get(i));
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(1, id_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, getExceptionNameBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, getExceptionMsgBytes());
      }
      for (int i = 0; i < stacktraces_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, stacktraces_.get(i));
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @Override
    protected Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static ExceptionPO parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ExceptionPO parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ExceptionPO parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ExceptionPO parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ExceptionPO parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static ExceptionPO parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static ExceptionPO parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static ExceptionPO parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static ExceptionPO parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static ExceptionPO parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(ExceptionPO prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code com.caibowen.prma.api.proto.ExceptionPO}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:com.caibowen.prma.api.proto.ExceptionPO)
        ExceptionPOOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return Exception.internal_static_com_caibowen_prma_api_proto_ExceptionPO_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return Exception.internal_static_com_caibowen_prma_api_proto_ExceptionPO_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ExceptionPO.class, Builder.class);
      }

      // Construct using com.caibowen.prma.api.proto.Exception.ExceptionPO.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
          getStacktracesFieldBuilder();
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        id_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000001);
        exceptionName_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        exceptionMsg_ = "";
        bitField0_ = (bitField0_ & ~0x00000004);
        if (stacktracesBuilder_ == null) {
          stacktraces_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000008);
        } else {
          stacktracesBuilder_.clear();
        }
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return Exception.internal_static_com_caibowen_prma_api_proto_ExceptionPO_descriptor;
      }

      public ExceptionPO getDefaultInstanceForType() {
        return ExceptionPO.getDefaultInstance();
      }

      public ExceptionPO build() {
        ExceptionPO result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public ExceptionPO buildPartial() {
        ExceptionPO result = new ExceptionPO(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.id_ = id_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.exceptionName_ = exceptionName_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.exceptionMsg_ = exceptionMsg_;
        if (stacktracesBuilder_ == null) {
          if (((bitField0_ & 0x00000008) == 0x00000008)) {
            stacktraces_ = java.util.Collections.unmodifiableList(stacktraces_);
            bitField0_ = (bitField0_ & ~0x00000008);
          }
          result.stacktraces_ = stacktraces_;
        } else {
          result.stacktraces_ = stacktracesBuilder_.build();
        }
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof ExceptionPO) {
          return mergeFrom((ExceptionPO)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ExceptionPO other) {
        if (other == ExceptionPO.getDefaultInstance()) return this;
        if (other.hasId()) {
          setId(other.getId());
        }
        if (other.hasExceptionName()) {
          bitField0_ |= 0x00000002;
          exceptionName_ = other.exceptionName_;
          onChanged();
        }
        if (other.hasExceptionMsg()) {
          bitField0_ |= 0x00000004;
          exceptionMsg_ = other.exceptionMsg_;
          onChanged();
        }
        if (stacktracesBuilder_ == null) {
          if (!other.stacktraces_.isEmpty()) {
            if (stacktraces_.isEmpty()) {
              stacktraces_ = other.stacktraces_;
              bitField0_ = (bitField0_ & ~0x00000008);
            } else {
              ensureStacktracesIsMutable();
              stacktraces_.addAll(other.stacktraces_);
            }
            onChanged();
          }
        } else {
          if (!other.stacktraces_.isEmpty()) {
            if (stacktracesBuilder_.isEmpty()) {
              stacktracesBuilder_.dispose();
              stacktracesBuilder_ = null;
              stacktraces_ = other.stacktraces_;
              bitField0_ = (bitField0_ & ~0x00000008);
              stacktracesBuilder_ = 
                com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders ?
                   getStacktracesFieldBuilder() : null;
            } else {
              stacktracesBuilder_.addAllMessages(other.stacktraces_);
            }
          }
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasId()) {
          
          return false;
        }
        if (!hasExceptionName()) {
          
          return false;
        }
        for (int i = 0; i < getStacktracesCount(); i++) {
          if (!getStacktraces(i).isInitialized()) {
            
            return false;
          }
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        ExceptionPO parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ExceptionPO) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private long id_ ;
      /**
       * <code>required uint64 id = 1;</code>
       */
      public boolean hasId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required uint64 id = 1;</code>
       */
      public long getId() {
        return id_;
      }
      /**
       * <code>required uint64 id = 1;</code>
       */
      public Builder setId(long value) {
        bitField0_ |= 0x00000001;
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required uint64 id = 1;</code>
       */
      public Builder clearId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        id_ = 0L;
        onChanged();
        return this;
      }

      private Object exceptionName_ = "";
      /**
       * <code>required string exception_name = 2;</code>
       */
      public boolean hasExceptionName() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required string exception_name = 2;</code>
       */
      public String getExceptionName() {
        Object ref = exceptionName_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            exceptionName_ = s;
          }
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>required string exception_name = 2;</code>
       */
      public com.google.protobuf.ByteString
          getExceptionNameBytes() {
        Object ref = exceptionName_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          exceptionName_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string exception_name = 2;</code>
       */
      public Builder setExceptionName(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        exceptionName_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string exception_name = 2;</code>
       */
      public Builder clearExceptionName() {
        bitField0_ = (bitField0_ & ~0x00000002);
        exceptionName_ = getDefaultInstance().getExceptionName();
        onChanged();
        return this;
      }
      /**
       * <code>required string exception_name = 2;</code>
       */
      public Builder setExceptionNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        exceptionName_ = value;
        onChanged();
        return this;
      }

      private Object exceptionMsg_ = "";
      /**
       * <code>optional string exception_msg = 3;</code>
       */
      public boolean hasExceptionMsg() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>optional string exception_msg = 3;</code>
       */
      public String getExceptionMsg() {
        Object ref = exceptionMsg_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            exceptionMsg_ = s;
          }
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>optional string exception_msg = 3;</code>
       */
      public com.google.protobuf.ByteString
          getExceptionMsgBytes() {
        Object ref = exceptionMsg_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          exceptionMsg_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string exception_msg = 3;</code>
       */
      public Builder setExceptionMsg(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        exceptionMsg_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string exception_msg = 3;</code>
       */
      public Builder clearExceptionMsg() {
        bitField0_ = (bitField0_ & ~0x00000004);
        exceptionMsg_ = getDefaultInstance().getExceptionMsg();
        onChanged();
        return this;
      }
      /**
       * <code>optional string exception_msg = 3;</code>
       */
      public Builder setExceptionMsgBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        exceptionMsg_ = value;
        onChanged();
        return this;
      }

      private java.util.List<StackTrace.StackTracePO> stacktraces_ =
        java.util.Collections.emptyList();
      private void ensureStacktracesIsMutable() {
        if (!((bitField0_ & 0x00000008) == 0x00000008)) {
          stacktraces_ = new java.util.ArrayList<StackTrace.StackTracePO>(stacktraces_);
          bitField0_ |= 0x00000008;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilder<
          StackTrace.StackTracePO, StackTrace.StackTracePO.Builder, StackTrace.StackTracePOOrBuilder> stacktracesBuilder_;

      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public java.util.List<StackTrace.StackTracePO> getStacktracesList() {
        if (stacktracesBuilder_ == null) {
          return java.util.Collections.unmodifiableList(stacktraces_);
        } else {
          return stacktracesBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public int getStacktracesCount() {
        if (stacktracesBuilder_ == null) {
          return stacktraces_.size();
        } else {
          return stacktracesBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public StackTrace.StackTracePO getStacktraces(int index) {
        if (stacktracesBuilder_ == null) {
          return stacktraces_.get(index);
        } else {
          return stacktracesBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public Builder setStacktraces(
          int index, StackTrace.StackTracePO value) {
        if (stacktracesBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureStacktracesIsMutable();
          stacktraces_.set(index, value);
          onChanged();
        } else {
          stacktracesBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public Builder setStacktraces(
          int index, StackTrace.StackTracePO.Builder builderForValue) {
        if (stacktracesBuilder_ == null) {
          ensureStacktracesIsMutable();
          stacktraces_.set(index, builderForValue.build());
          onChanged();
        } else {
          stacktracesBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public Builder addStacktraces(StackTrace.StackTracePO value) {
        if (stacktracesBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureStacktracesIsMutable();
          stacktraces_.add(value);
          onChanged();
        } else {
          stacktracesBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public Builder addStacktraces(
          int index, StackTrace.StackTracePO value) {
        if (stacktracesBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureStacktracesIsMutable();
          stacktraces_.add(index, value);
          onChanged();
        } else {
          stacktracesBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public Builder addStacktraces(
          StackTrace.StackTracePO.Builder builderForValue) {
        if (stacktracesBuilder_ == null) {
          ensureStacktracesIsMutable();
          stacktraces_.add(builderForValue.build());
          onChanged();
        } else {
          stacktracesBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public Builder addStacktraces(
          int index, StackTrace.StackTracePO.Builder builderForValue) {
        if (stacktracesBuilder_ == null) {
          ensureStacktracesIsMutable();
          stacktraces_.add(index, builderForValue.build());
          onChanged();
        } else {
          stacktracesBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public Builder addAllStacktraces(
          Iterable<? extends StackTrace.StackTracePO> values) {
        if (stacktracesBuilder_ == null) {
          ensureStacktracesIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, stacktraces_);
          onChanged();
        } else {
          stacktracesBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public Builder clearStacktraces() {
        if (stacktracesBuilder_ == null) {
          stacktraces_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000008);
          onChanged();
        } else {
          stacktracesBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public Builder removeStacktraces(int index) {
        if (stacktracesBuilder_ == null) {
          ensureStacktracesIsMutable();
          stacktraces_.remove(index);
          onChanged();
        } else {
          stacktracesBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public StackTrace.StackTracePO.Builder getStacktracesBuilder(
          int index) {
        return getStacktracesFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public StackTrace.StackTracePOOrBuilder getStacktracesOrBuilder(
          int index) {
        if (stacktracesBuilder_ == null) {
          return stacktraces_.get(index);  } else {
          return stacktracesBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public java.util.List<? extends StackTrace.StackTracePOOrBuilder>
           getStacktracesOrBuilderList() {
        if (stacktracesBuilder_ != null) {
          return stacktracesBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(stacktraces_);
        }
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public StackTrace.StackTracePO.Builder addStacktracesBuilder() {
        return getStacktracesFieldBuilder().addBuilder(
            StackTrace.StackTracePO.getDefaultInstance());
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public StackTrace.StackTracePO.Builder addStacktracesBuilder(
          int index) {
        return getStacktracesFieldBuilder().addBuilder(
            index, StackTrace.StackTracePO.getDefaultInstance());
      }
      /**
       * <code>repeated .com.caibowen.prma.api.proto.StackTracePO stacktraces = 4;</code>
       */
      public java.util.List<StackTrace.StackTracePO.Builder>
           getStacktracesBuilderList() {
        return getStacktracesFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilder<
          StackTrace.StackTracePO, StackTrace.StackTracePO.Builder, StackTrace.StackTracePOOrBuilder>
          getStacktracesFieldBuilder() {
        if (stacktracesBuilder_ == null) {
          stacktracesBuilder_ = new com.google.protobuf.RepeatedFieldBuilder<
              StackTrace.StackTracePO, StackTrace.StackTracePO.Builder, StackTrace.StackTracePOOrBuilder>(
                  stacktraces_,
                  ((bitField0_ & 0x00000008) == 0x00000008),
                  getParentForChildren(),
                  isClean());
          stacktraces_ = null;
        }
        return stacktracesBuilder_;
      }

      // @@protoc_insertion_point(builder_scope:com.caibowen.prma.api.proto.ExceptionPO)
    }

    static {
      defaultInstance = new ExceptionPO(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:com.caibowen.prma.api.proto.ExceptionPO)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_caibowen_prma_api_proto_ExceptionPO_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_caibowen_prma_api_proto_ExceptionPO_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\017Exception.proto\022\033com.caibowen.prma.api" +
      ".proto\032\020StackTrace.proto\"\210\001\n\013ExceptionPO" +
      "\022\n\n\002id\030\001 \002(\004\022\026\n\016exception_name\030\002 \002(\t\022\025\n\r" +
      "exception_msg\030\003 \001(\t\022>\n\013stacktraces\030\004 \003(\013" +
      "2).com.caibowen.prma.api.proto.StackTrac" +
      "ePOP\000"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          StackTrace.getDescriptor(),
        }, assigner);
    internal_static_com_caibowen_prma_api_proto_ExceptionPO_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_caibowen_prma_api_proto_ExceptionPO_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_com_caibowen_prma_api_proto_ExceptionPO_descriptor,
        new String[] { "Id", "ExceptionName", "ExceptionMsg", "Stacktraces", });
    StackTrace.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
