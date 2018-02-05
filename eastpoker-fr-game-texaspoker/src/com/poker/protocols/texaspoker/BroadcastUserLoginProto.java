// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: src/com/poker/protocols/texaspoker/proto/BroadcastUserLogin.proto

package com.poker.protocols.texaspoker;

public final class BroadcastUserLoginProto {
  private BroadcastUserLoginProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface BroadcastUserLoginOrBuilder extends
      // @@protoc_insertion_point(interface_extends:com.poker.protocols.texaspoker.proto.BroadcastUserLogin)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>.com.poker.protocols.texaspoker.proto.GameUser users = 1;</code>
     */
    boolean hasUsers();
    /**
     * <code>.com.poker.protocols.texaspoker.proto.GameUser users = 1;</code>
     */
    com.poker.protocols.texaspoker.GameUserProto.GameUser getUsers();
    /**
     * <code>.com.poker.protocols.texaspoker.proto.GameUser users = 1;</code>
     */
    com.poker.protocols.texaspoker.GameUserProto.GameUserOrBuilder getUsersOrBuilder();
  }
  /**
   * Protobuf type {@code com.poker.protocols.texaspoker.proto.BroadcastUserLogin}
   */
  public  static final class BroadcastUserLogin extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:com.poker.protocols.texaspoker.proto.BroadcastUserLogin)
      BroadcastUserLoginOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use BroadcastUserLogin.newBuilder() to construct.
    private BroadcastUserLogin(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private BroadcastUserLogin() {
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private BroadcastUserLogin(
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
            case 10: {
              com.poker.protocols.texaspoker.GameUserProto.GameUser.Builder subBuilder = null;
              if (users_ != null) {
                subBuilder = users_.toBuilder();
              }
              users_ = input.readMessage(com.poker.protocols.texaspoker.GameUserProto.GameUser.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(users_);
                users_ = subBuilder.buildPartial();
              }

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
      return com.poker.protocols.texaspoker.BroadcastUserLoginProto.internal_static_com_poker_protocols_texaspoker_proto_BroadcastUserLogin_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.poker.protocols.texaspoker.BroadcastUserLoginProto.internal_static_com_poker_protocols_texaspoker_proto_BroadcastUserLogin_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin.class, com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin.Builder.class);
    }

    public static final int USERS_FIELD_NUMBER = 1;
    private com.poker.protocols.texaspoker.GameUserProto.GameUser users_;
    /**
     * <code>.com.poker.protocols.texaspoker.proto.GameUser users = 1;</code>
     */
    public boolean hasUsers() {
      return users_ != null;
    }
    /**
     * <code>.com.poker.protocols.texaspoker.proto.GameUser users = 1;</code>
     */
    public com.poker.protocols.texaspoker.GameUserProto.GameUser getUsers() {
      return users_ == null ? com.poker.protocols.texaspoker.GameUserProto.GameUser.getDefaultInstance() : users_;
    }
    /**
     * <code>.com.poker.protocols.texaspoker.proto.GameUser users = 1;</code>
     */
    public com.poker.protocols.texaspoker.GameUserProto.GameUserOrBuilder getUsersOrBuilder() {
      return getUsers();
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
      if (users_ != null) {
        output.writeMessage(1, getUsers());
      }
      unknownFields.writeTo(output);
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (users_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, getUsers());
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
      if (!(obj instanceof com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin)) {
        return super.equals(obj);
      }
      com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin other = (com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin) obj;

      boolean result = true;
      result = result && (hasUsers() == other.hasUsers());
      if (hasUsers()) {
        result = result && getUsers()
            .equals(other.getUsers());
      }
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
      if (hasUsers()) {
        hash = (37 * hash) + USERS_FIELD_NUMBER;
        hash = (53 * hash) + getUsers().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    //-----------------------------------------------------
    public static com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin parseFrom(byte[] data,int offset ,int length)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data,offset,length);
        }
    //-----------------------------------------------------
    public static com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    //-----------------------------------------------------
    public static com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin parseFrom(
    		byte[] data,int offset ,int length,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data,offset,length, extensionRegistry);
        }
   //-----------------------------------------------------
    public static com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin parseFrom(
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
    public static Builder newBuilder(com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin prototype) {
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
     * Protobuf type {@code com.poker.protocols.texaspoker.proto.BroadcastUserLogin}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:com.poker.protocols.texaspoker.proto.BroadcastUserLogin)
        com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLoginOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.poker.protocols.texaspoker.BroadcastUserLoginProto.internal_static_com_poker_protocols_texaspoker_proto_BroadcastUserLogin_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.poker.protocols.texaspoker.BroadcastUserLoginProto.internal_static_com_poker_protocols_texaspoker_proto_BroadcastUserLogin_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin.class, com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin.Builder.class);
      }

      // Construct using com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin.newBuilder()
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
        if (usersBuilder_ == null) {
          users_ = null;
        } else {
          users_ = null;
          usersBuilder_ = null;
        }
        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.poker.protocols.texaspoker.BroadcastUserLoginProto.internal_static_com_poker_protocols_texaspoker_proto_BroadcastUserLogin_descriptor;
      }

      public com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin getDefaultInstanceForType() {
        return com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin.getDefaultInstance();
      }

      public com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin build() {
        com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin buildPartial() {
        com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin result = new com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin(this);
        if (usersBuilder_ == null) {
          result.users_ = users_;
        } else {
          result.users_ = usersBuilder_.build();
        }
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
        if (other instanceof com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin) {
          return mergeFrom((com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin other) {
        if (other == com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin.getDefaultInstance()) return this;
        if (other.hasUsers()) {
          mergeUsers(other.getUsers());
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
        com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private com.poker.protocols.texaspoker.GameUserProto.GameUser users_ = null;
      private com.google.protobuf.SingleFieldBuilderV3<
          com.poker.protocols.texaspoker.GameUserProto.GameUser, com.poker.protocols.texaspoker.GameUserProto.GameUser.Builder, com.poker.protocols.texaspoker.GameUserProto.GameUserOrBuilder> usersBuilder_;
      /**
       * <code>.com.poker.protocols.texaspoker.proto.GameUser users = 1;</code>
       */
      public boolean hasUsers() {
        return usersBuilder_ != null || users_ != null;
      }
      /**
       * <code>.com.poker.protocols.texaspoker.proto.GameUser users = 1;</code>
       */
      public com.poker.protocols.texaspoker.GameUserProto.GameUser getUsers() {
        if (usersBuilder_ == null) {
          return users_ == null ? com.poker.protocols.texaspoker.GameUserProto.GameUser.getDefaultInstance() : users_;
        } else {
          return usersBuilder_.getMessage();
        }
      }
      /**
       * <code>.com.poker.protocols.texaspoker.proto.GameUser users = 1;</code>
       */
      public Builder setUsers(com.poker.protocols.texaspoker.GameUserProto.GameUser value) {
        if (usersBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          users_ = value;
          onChanged();
        } else {
          usersBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>.com.poker.protocols.texaspoker.proto.GameUser users = 1;</code>
       */
      public Builder setUsers(
          com.poker.protocols.texaspoker.GameUserProto.GameUser.Builder builderForValue) {
        if (usersBuilder_ == null) {
          users_ = builderForValue.build();
          onChanged();
        } else {
          usersBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>.com.poker.protocols.texaspoker.proto.GameUser users = 1;</code>
       */
      public Builder mergeUsers(com.poker.protocols.texaspoker.GameUserProto.GameUser value) {
        if (usersBuilder_ == null) {
          if (users_ != null) {
            users_ =
              com.poker.protocols.texaspoker.GameUserProto.GameUser.newBuilder(users_).mergeFrom(value).buildPartial();
          } else {
            users_ = value;
          }
          onChanged();
        } else {
          usersBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>.com.poker.protocols.texaspoker.proto.GameUser users = 1;</code>
       */
      public Builder clearUsers() {
        if (usersBuilder_ == null) {
          users_ = null;
          onChanged();
        } else {
          users_ = null;
          usersBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>.com.poker.protocols.texaspoker.proto.GameUser users = 1;</code>
       */
      public com.poker.protocols.texaspoker.GameUserProto.GameUser.Builder getUsersBuilder() {
        
        onChanged();
        return getUsersFieldBuilder().getBuilder();
      }
      /**
       * <code>.com.poker.protocols.texaspoker.proto.GameUser users = 1;</code>
       */
      public com.poker.protocols.texaspoker.GameUserProto.GameUserOrBuilder getUsersOrBuilder() {
        if (usersBuilder_ != null) {
          return usersBuilder_.getMessageOrBuilder();
        } else {
          return users_ == null ?
              com.poker.protocols.texaspoker.GameUserProto.GameUser.getDefaultInstance() : users_;
        }
      }
      /**
       * <code>.com.poker.protocols.texaspoker.proto.GameUser users = 1;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          com.poker.protocols.texaspoker.GameUserProto.GameUser, com.poker.protocols.texaspoker.GameUserProto.GameUser.Builder, com.poker.protocols.texaspoker.GameUserProto.GameUserOrBuilder> 
          getUsersFieldBuilder() {
        if (usersBuilder_ == null) {
          usersBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              com.poker.protocols.texaspoker.GameUserProto.GameUser, com.poker.protocols.texaspoker.GameUserProto.GameUser.Builder, com.poker.protocols.texaspoker.GameUserProto.GameUserOrBuilder>(
                  getUsers(),
                  getParentForChildren(),
                  isClean());
          users_ = null;
        }
        return usersBuilder_;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFieldsProto3(unknownFields);
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:com.poker.protocols.texaspoker.proto.BroadcastUserLogin)
    }

    // @@protoc_insertion_point(class_scope:com.poker.protocols.texaspoker.proto.BroadcastUserLogin)
    private static final com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin();
    }

    public static com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<BroadcastUserLogin>
        PARSER = new com.google.protobuf.AbstractParser<BroadcastUserLogin>() {
      public BroadcastUserLogin parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new BroadcastUserLogin(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<BroadcastUserLogin> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<BroadcastUserLogin> getParserForType() {
      return PARSER;
    }

    public com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_poker_protocols_texaspoker_proto_BroadcastUserLogin_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_poker_protocols_texaspoker_proto_BroadcastUserLogin_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\nAsrc/com/poker/protocols/texaspoker/pro" +
      "to/BroadcastUserLogin.proto\022$com.poker.p" +
      "rotocols.texaspoker.proto\0327src/com/poker" +
      "/protocols/texaspoker/proto/GameUser.pro" +
      "to\"S\n\022BroadcastUserLogin\022=\n\005users\030\001 \001(\0132" +
      "..com.poker.protocols.texaspoker.proto.G" +
      "ameUserB9\n\036com.poker.protocols.texaspoke" +
      "rB\027BroadcastUserLoginProtob\006proto3"
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
          com.poker.protocols.texaspoker.GameUserProto.getDescriptor(),
        }, assigner);
    internal_static_com_poker_protocols_texaspoker_proto_BroadcastUserLogin_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_poker_protocols_texaspoker_proto_BroadcastUserLogin_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_poker_protocols_texaspoker_proto_BroadcastUserLogin_descriptor,
        new java.lang.String[] { "Users", });
    com.poker.protocols.texaspoker.GameUserProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
