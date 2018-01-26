// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: src/com/poker/protocols/server/proto/DispatchChain.proto

package com.poker.protocols.server;

public final class DispatchChainProto {
  private DispatchChainProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface DispatchChainOrBuilder extends
      // @@protoc_insertion_point(interface_extends:com.poker.protocols.server.proto.DispatchChain)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int32 src_server_type = 1;</code>
     */
    int getSrcServerType();

    /**
     * <code>int32 src_server_id = 2;</code>
     */
    int getSrcServerId();

    /**
     * <code>int32 dst_server_type = 3;</code>
     */
    int getDstServerType();

    /**
     * <code>int32 dst_server_id = 4;</code>
     */
    int getDstServerId();

    /**
     * <code>int32 dst_game_group = 5;</code>
     */
    int getDstGameGroup();

    /**
     * <code>int32 dst_match_group = 6;</code>
     */
    int getDstMatchGroup();

    /**
     * <code>int64 uid = 7;</code>
     */
    long getUid();

    /**
     * <code>int32 cmd = 8;</code>
     */
    int getCmd();

    /**
     * <code>int32 dispatch_type = 9;</code>
     */
    int getDispatchType();
  }
  /**
   * Protobuf type {@code com.poker.protocols.server.proto.DispatchChain}
   */
  public  static final class DispatchChain extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:com.poker.protocols.server.proto.DispatchChain)
      DispatchChainOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use DispatchChain.newBuilder() to construct.
    private DispatchChain(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private DispatchChain() {
      srcServerType_ = 0;
      srcServerId_ = 0;
      dstServerType_ = 0;
      dstServerId_ = 0;
      dstGameGroup_ = 0;
      dstMatchGroup_ = 0;
      uid_ = 0L;
      cmd_ = 0;
      dispatchType_ = 0;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private DispatchChain(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
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
              if (!parseUnknownFieldProto3(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {

              srcServerType_ = input.readInt32();
              break;
            }
            case 16: {

              srcServerId_ = input.readInt32();
              break;
            }
            case 24: {

              dstServerType_ = input.readInt32();
              break;
            }
            case 32: {

              dstServerId_ = input.readInt32();
              break;
            }
            case 40: {

              dstGameGroup_ = input.readInt32();
              break;
            }
            case 48: {

              dstMatchGroup_ = input.readInt32();
              break;
            }
            case 56: {

              uid_ = input.readInt64();
              break;
            }
            case 64: {

              cmd_ = input.readInt32();
              break;
            }
            case 72: {

              dispatchType_ = input.readInt32();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.poker.protocols.server.DispatchChainProto.internal_static_com_poker_protocols_server_proto_DispatchChain_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.poker.protocols.server.DispatchChainProto.internal_static_com_poker_protocols_server_proto_DispatchChain_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.poker.protocols.server.DispatchChainProto.DispatchChain.class, com.poker.protocols.server.DispatchChainProto.DispatchChain.Builder.class);
    }

    public static final int SRC_SERVER_TYPE_FIELD_NUMBER = 1;
    private int srcServerType_;
    /**
     * <code>int32 src_server_type = 1;</code>
     */
    public int getSrcServerType() {
      return srcServerType_;
    }

    public static final int SRC_SERVER_ID_FIELD_NUMBER = 2;
    private int srcServerId_;
    /**
     * <code>int32 src_server_id = 2;</code>
     */
    public int getSrcServerId() {
      return srcServerId_;
    }

    public static final int DST_SERVER_TYPE_FIELD_NUMBER = 3;
    private int dstServerType_;
    /**
     * <code>int32 dst_server_type = 3;</code>
     */
    public int getDstServerType() {
      return dstServerType_;
    }

    public static final int DST_SERVER_ID_FIELD_NUMBER = 4;
    private int dstServerId_;
    /**
     * <code>int32 dst_server_id = 4;</code>
     */
    public int getDstServerId() {
      return dstServerId_;
    }

    public static final int DST_GAME_GROUP_FIELD_NUMBER = 5;
    private int dstGameGroup_;
    /**
     * <code>int32 dst_game_group = 5;</code>
     */
    public int getDstGameGroup() {
      return dstGameGroup_;
    }

    public static final int DST_MATCH_GROUP_FIELD_NUMBER = 6;
    private int dstMatchGroup_;
    /**
     * <code>int32 dst_match_group = 6;</code>
     */
    public int getDstMatchGroup() {
      return dstMatchGroup_;
    }

    public static final int UID_FIELD_NUMBER = 7;
    private long uid_;
    /**
     * <code>int64 uid = 7;</code>
     */
    public long getUid() {
      return uid_;
    }

    public static final int CMD_FIELD_NUMBER = 8;
    private int cmd_;
    /**
     * <code>int32 cmd = 8;</code>
     */
    public int getCmd() {
      return cmd_;
    }

    public static final int DISPATCH_TYPE_FIELD_NUMBER = 9;
    private int dispatchType_;
    /**
     * <code>int32 dispatch_type = 9;</code>
     */
    public int getDispatchType() {
      return dispatchType_;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (srcServerType_ != 0) {
        output.writeInt32(1, srcServerType_);
      }
      if (srcServerId_ != 0) {
        output.writeInt32(2, srcServerId_);
      }
      if (dstServerType_ != 0) {
        output.writeInt32(3, dstServerType_);
      }
      if (dstServerId_ != 0) {
        output.writeInt32(4, dstServerId_);
      }
      if (dstGameGroup_ != 0) {
        output.writeInt32(5, dstGameGroup_);
      }
      if (dstMatchGroup_ != 0) {
        output.writeInt32(6, dstMatchGroup_);
      }
      if (uid_ != 0L) {
        output.writeInt64(7, uid_);
      }
      if (cmd_ != 0) {
        output.writeInt32(8, cmd_);
      }
      if (dispatchType_ != 0) {
        output.writeInt32(9, dispatchType_);
      }
      unknownFields.writeTo(output);
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (srcServerType_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, srcServerType_);
      }
      if (srcServerId_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, srcServerId_);
      }
      if (dstServerType_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, dstServerType_);
      }
      if (dstServerId_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, dstServerId_);
      }
      if (dstGameGroup_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(5, dstGameGroup_);
      }
      if (dstMatchGroup_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(6, dstMatchGroup_);
      }
      if (uid_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(7, uid_);
      }
      if (cmd_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(8, cmd_);
      }
      if (dispatchType_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(9, dispatchType_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.poker.protocols.server.DispatchChainProto.DispatchChain)) {
        return super.equals(obj);
      }
      com.poker.protocols.server.DispatchChainProto.DispatchChain other = (com.poker.protocols.server.DispatchChainProto.DispatchChain) obj;

      boolean result = true;
      result = result && (getSrcServerType()
          == other.getSrcServerType());
      result = result && (getSrcServerId()
          == other.getSrcServerId());
      result = result && (getDstServerType()
          == other.getDstServerType());
      result = result && (getDstServerId()
          == other.getDstServerId());
      result = result && (getDstGameGroup()
          == other.getDstGameGroup());
      result = result && (getDstMatchGroup()
          == other.getDstMatchGroup());
      result = result && (getUid()
          == other.getUid());
      result = result && (getCmd()
          == other.getCmd());
      result = result && (getDispatchType()
          == other.getDispatchType());
      result = result && unknownFields.equals(other.unknownFields);
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + SRC_SERVER_TYPE_FIELD_NUMBER;
      hash = (53 * hash) + getSrcServerType();
      hash = (37 * hash) + SRC_SERVER_ID_FIELD_NUMBER;
      hash = (53 * hash) + getSrcServerId();
      hash = (37 * hash) + DST_SERVER_TYPE_FIELD_NUMBER;
      hash = (53 * hash) + getDstServerType();
      hash = (37 * hash) + DST_SERVER_ID_FIELD_NUMBER;
      hash = (53 * hash) + getDstServerId();
      hash = (37 * hash) + DST_GAME_GROUP_FIELD_NUMBER;
      hash = (53 * hash) + getDstGameGroup();
      hash = (37 * hash) + DST_MATCH_GROUP_FIELD_NUMBER;
      hash = (53 * hash) + getDstMatchGroup();
      hash = (37 * hash) + UID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getUid());
      hash = (37 * hash) + CMD_FIELD_NUMBER;
      hash = (53 * hash) + getCmd();
      hash = (37 * hash) + DISPATCH_TYPE_FIELD_NUMBER;
      hash = (53 * hash) + getDispatchType();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.poker.protocols.server.DispatchChainProto.DispatchChain parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.poker.protocols.server.DispatchChainProto.DispatchChain parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.poker.protocols.server.DispatchChainProto.DispatchChain parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.poker.protocols.server.DispatchChainProto.DispatchChain parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.poker.protocols.server.DispatchChainProto.DispatchChain parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    //-----------------------------------------------------
    public static com.poker.protocols.server.DispatchChainProto.DispatchChain parseFrom(byte[] data,int offset ,int length)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data,offset,length);
        }
    //-----------------------------------------------------
    public static com.poker.protocols.server.DispatchChainProto.DispatchChain parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    //-----------------------------------------------------
    public static com.poker.protocols.server.DispatchChainProto.DispatchChain parseFrom(
    		byte[] data,int offset ,int length,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data,offset,length, extensionRegistry);
        }
   //-----------------------------------------------------
    public static com.poker.protocols.server.DispatchChainProto.DispatchChain parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.poker.protocols.server.DispatchChainProto.DispatchChain parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.poker.protocols.server.DispatchChainProto.DispatchChain parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.poker.protocols.server.DispatchChainProto.DispatchChain parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.poker.protocols.server.DispatchChainProto.DispatchChain parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.poker.protocols.server.DispatchChainProto.DispatchChain parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.poker.protocols.server.DispatchChainProto.DispatchChain prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code com.poker.protocols.server.proto.DispatchChain}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:com.poker.protocols.server.proto.DispatchChain)
        com.poker.protocols.server.DispatchChainProto.DispatchChainOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.poker.protocols.server.DispatchChainProto.internal_static_com_poker_protocols_server_proto_DispatchChain_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.poker.protocols.server.DispatchChainProto.internal_static_com_poker_protocols_server_proto_DispatchChain_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.poker.protocols.server.DispatchChainProto.DispatchChain.class, com.poker.protocols.server.DispatchChainProto.DispatchChain.Builder.class);
      }

      // Construct using com.poker.protocols.server.DispatchChainProto.DispatchChain.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        srcServerType_ = 0;

        srcServerId_ = 0;

        dstServerType_ = 0;

        dstServerId_ = 0;

        dstGameGroup_ = 0;

        dstMatchGroup_ = 0;

        uid_ = 0L;

        cmd_ = 0;

        dispatchType_ = 0;

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.poker.protocols.server.DispatchChainProto.internal_static_com_poker_protocols_server_proto_DispatchChain_descriptor;
      }

      public com.poker.protocols.server.DispatchChainProto.DispatchChain getDefaultInstanceForType() {
        return com.poker.protocols.server.DispatchChainProto.DispatchChain.getDefaultInstance();
      }

      public com.poker.protocols.server.DispatchChainProto.DispatchChain build() {
        com.poker.protocols.server.DispatchChainProto.DispatchChain result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.poker.protocols.server.DispatchChainProto.DispatchChain buildPartial() {
        com.poker.protocols.server.DispatchChainProto.DispatchChain result = new com.poker.protocols.server.DispatchChainProto.DispatchChain(this);
        result.srcServerType_ = srcServerType_;
        result.srcServerId_ = srcServerId_;
        result.dstServerType_ = dstServerType_;
        result.dstServerId_ = dstServerId_;
        result.dstGameGroup_ = dstGameGroup_;
        result.dstMatchGroup_ = dstMatchGroup_;
        result.uid_ = uid_;
        result.cmd_ = cmd_;
        result.dispatchType_ = dispatchType_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.poker.protocols.server.DispatchChainProto.DispatchChain) {
          return mergeFrom((com.poker.protocols.server.DispatchChainProto.DispatchChain)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.poker.protocols.server.DispatchChainProto.DispatchChain other) {
        if (other == com.poker.protocols.server.DispatchChainProto.DispatchChain.getDefaultInstance()) return this;
        if (other.getSrcServerType() != 0) {
          setSrcServerType(other.getSrcServerType());
        }
        if (other.getSrcServerId() != 0) {
          setSrcServerId(other.getSrcServerId());
        }
        if (other.getDstServerType() != 0) {
          setDstServerType(other.getDstServerType());
        }
        if (other.getDstServerId() != 0) {
          setDstServerId(other.getDstServerId());
        }
        if (other.getDstGameGroup() != 0) {
          setDstGameGroup(other.getDstGameGroup());
        }
        if (other.getDstMatchGroup() != 0) {
          setDstMatchGroup(other.getDstMatchGroup());
        }
        if (other.getUid() != 0L) {
          setUid(other.getUid());
        }
        if (other.getCmd() != 0) {
          setCmd(other.getCmd());
        }
        if (other.getDispatchType() != 0) {
          setDispatchType(other.getDispatchType());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.poker.protocols.server.DispatchChainProto.DispatchChain parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.poker.protocols.server.DispatchChainProto.DispatchChain) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int srcServerType_ ;
      /**
       * <code>int32 src_server_type = 1;</code>
       */
      public int getSrcServerType() {
        return srcServerType_;
      }
      /**
       * <code>int32 src_server_type = 1;</code>
       */
      public Builder setSrcServerType(int value) {
        
        srcServerType_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 src_server_type = 1;</code>
       */
      public Builder clearSrcServerType() {
        
        srcServerType_ = 0;
        onChanged();
        return this;
      }

      private int srcServerId_ ;
      /**
       * <code>int32 src_server_id = 2;</code>
       */
      public int getSrcServerId() {
        return srcServerId_;
      }
      /**
       * <code>int32 src_server_id = 2;</code>
       */
      public Builder setSrcServerId(int value) {
        
        srcServerId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 src_server_id = 2;</code>
       */
      public Builder clearSrcServerId() {
        
        srcServerId_ = 0;
        onChanged();
        return this;
      }

      private int dstServerType_ ;
      /**
       * <code>int32 dst_server_type = 3;</code>
       */
      public int getDstServerType() {
        return dstServerType_;
      }
      /**
       * <code>int32 dst_server_type = 3;</code>
       */
      public Builder setDstServerType(int value) {
        
        dstServerType_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 dst_server_type = 3;</code>
       */
      public Builder clearDstServerType() {
        
        dstServerType_ = 0;
        onChanged();
        return this;
      }

      private int dstServerId_ ;
      /**
       * <code>int32 dst_server_id = 4;</code>
       */
      public int getDstServerId() {
        return dstServerId_;
      }
      /**
       * <code>int32 dst_server_id = 4;</code>
       */
      public Builder setDstServerId(int value) {
        
        dstServerId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 dst_server_id = 4;</code>
       */
      public Builder clearDstServerId() {
        
        dstServerId_ = 0;
        onChanged();
        return this;
      }

      private int dstGameGroup_ ;
      /**
       * <code>int32 dst_game_group = 5;</code>
       */
      public int getDstGameGroup() {
        return dstGameGroup_;
      }
      /**
       * <code>int32 dst_game_group = 5;</code>
       */
      public Builder setDstGameGroup(int value) {
        
        dstGameGroup_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 dst_game_group = 5;</code>
       */
      public Builder clearDstGameGroup() {
        
        dstGameGroup_ = 0;
        onChanged();
        return this;
      }

      private int dstMatchGroup_ ;
      /**
       * <code>int32 dst_match_group = 6;</code>
       */
      public int getDstMatchGroup() {
        return dstMatchGroup_;
      }
      /**
       * <code>int32 dst_match_group = 6;</code>
       */
      public Builder setDstMatchGroup(int value) {
        
        dstMatchGroup_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 dst_match_group = 6;</code>
       */
      public Builder clearDstMatchGroup() {
        
        dstMatchGroup_ = 0;
        onChanged();
        return this;
      }

      private long uid_ ;
      /**
       * <code>int64 uid = 7;</code>
       */
      public long getUid() {
        return uid_;
      }
      /**
       * <code>int64 uid = 7;</code>
       */
      public Builder setUid(long value) {
        
        uid_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 uid = 7;</code>
       */
      public Builder clearUid() {
        
        uid_ = 0L;
        onChanged();
        return this;
      }

      private int cmd_ ;
      /**
       * <code>int32 cmd = 8;</code>
       */
      public int getCmd() {
        return cmd_;
      }
      /**
       * <code>int32 cmd = 8;</code>
       */
      public Builder setCmd(int value) {
        
        cmd_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 cmd = 8;</code>
       */
      public Builder clearCmd() {
        
        cmd_ = 0;
        onChanged();
        return this;
      }

      private int dispatchType_ ;
      /**
       * <code>int32 dispatch_type = 9;</code>
       */
      public int getDispatchType() {
        return dispatchType_;
      }
      /**
       * <code>int32 dispatch_type = 9;</code>
       */
      public Builder setDispatchType(int value) {
        
        dispatchType_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 dispatch_type = 9;</code>
       */
      public Builder clearDispatchType() {
        
        dispatchType_ = 0;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFieldsProto3(unknownFields);
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:com.poker.protocols.server.proto.DispatchChain)
    }

    // @@protoc_insertion_point(class_scope:com.poker.protocols.server.proto.DispatchChain)
    private static final com.poker.protocols.server.DispatchChainProto.DispatchChain DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.poker.protocols.server.DispatchChainProto.DispatchChain();
    }

    public static com.poker.protocols.server.DispatchChainProto.DispatchChain getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<DispatchChain>
        PARSER = new com.google.protobuf.AbstractParser<DispatchChain>() {
      public DispatchChain parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new DispatchChain(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<DispatchChain> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<DispatchChain> getParserForType() {
      return PARSER;
    }

    public com.poker.protocols.server.DispatchChainProto.DispatchChain getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_poker_protocols_server_proto_DispatchChain_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_poker_protocols_server_proto_DispatchChain_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n8src/com/poker/protocols/server/proto/D" +
      "ispatchChain.proto\022 com.poker.protocols." +
      "server.proto\"\321\001\n\rDispatchChain\022\027\n\017src_se" +
      "rver_type\030\001 \001(\005\022\025\n\rsrc_server_id\030\002 \001(\005\022\027" +
      "\n\017dst_server_type\030\003 \001(\005\022\025\n\rdst_server_id" +
      "\030\004 \001(\005\022\026\n\016dst_game_group\030\005 \001(\005\022\027\n\017dst_ma" +
      "tch_group\030\006 \001(\005\022\013\n\003uid\030\007 \001(\003\022\013\n\003cmd\030\010 \001(" +
      "\005\022\025\n\rdispatch_type\030\t \001(\005B0\n\032com.poker.pr" +
      "otocols.serverB\022DispatchChainProtob\006prot" +
      "o3"
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
        }, assigner);
    internal_static_com_poker_protocols_server_proto_DispatchChain_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_poker_protocols_server_proto_DispatchChain_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_poker_protocols_server_proto_DispatchChain_descriptor,
        new java.lang.String[] { "SrcServerType", "SrcServerId", "DstServerType", "DstServerId", "DstGameGroup", "DstMatchGroup", "Uid", "Cmd", "DispatchType", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
