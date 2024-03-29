// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: src/protobuf/KeyValueRequest.proto

package ca.NetSysLab.ProtocolBuffers;

public final class KeyValueRequest {
  private KeyValueRequest() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface KVRequestOrBuilder extends
      // @@protoc_insertion_point(interface_extends:KVRequest)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>uint32 command = 1;</code>
     * @return The command.
     */
    int getCommand();

    /**
     * <code>optional bytes key = 2;</code>
     * @return Whether the key field is set.
     */
    boolean hasKey();
    /**
     * <code>optional bytes key = 2;</code>
     * @return The key.
     */
    com.google.protobuf.ByteString getKey();

    /**
     * <code>optional bytes value = 3;</code>
     * @return Whether the value field is set.
     */
    boolean hasValue();
    /**
     * <code>optional bytes value = 3;</code>
     * @return The value.
     */
    com.google.protobuf.ByteString getValue();

    /**
     * <code>optional int32 version = 4;</code>
     * @return Whether the version field is set.
     */
    boolean hasVersion();
    /**
     * <code>optional int32 version = 4;</code>
     * @return The version.
     */
    int getVersion();

    /**
     * <code>optional .InternalRequestWrapper ir = 100;</code>
     * @return Whether the ir field is set.
     */
    boolean hasIr();
    /**
     * <code>optional .InternalRequestWrapper ir = 100;</code>
     * @return The ir.
     */
    ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper getIr();
    /**
     * <code>optional .InternalRequestWrapper ir = 100;</code>
     */
    ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapperOrBuilder getIrOrBuilder();
  }
  /**
   * Protobuf type {@code KVRequest}
   */
  public static final class KVRequest extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:KVRequest)
      KVRequestOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use KVRequest.newBuilder() to construct.
    private KVRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private KVRequest() {
      key_ = com.google.protobuf.ByteString.EMPTY;
      value_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new KVRequest();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return ca.NetSysLab.ProtocolBuffers.KeyValueRequest.internal_static_KVRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ca.NetSysLab.ProtocolBuffers.KeyValueRequest.internal_static_KVRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest.class, ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest.Builder.class);
    }

    private int bitField0_;
    public static final int COMMAND_FIELD_NUMBER = 1;
    private int command_;
    /**
     * <code>uint32 command = 1;</code>
     * @return The command.
     */
    @java.lang.Override
    public int getCommand() {
      return command_;
    }

    public static final int KEY_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString key_;
    /**
     * <code>optional bytes key = 2;</code>
     * @return Whether the key field is set.
     */
    @java.lang.Override
    public boolean hasKey() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>optional bytes key = 2;</code>
     * @return The key.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getKey() {
      return key_;
    }

    public static final int VALUE_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString value_;
    /**
     * <code>optional bytes value = 3;</code>
     * @return Whether the value field is set.
     */
    @java.lang.Override
    public boolean hasValue() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>optional bytes value = 3;</code>
     * @return The value.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getValue() {
      return value_;
    }

    public static final int VERSION_FIELD_NUMBER = 4;
    private int version_;
    /**
     * <code>optional int32 version = 4;</code>
     * @return Whether the version field is set.
     */
    @java.lang.Override
    public boolean hasVersion() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>optional int32 version = 4;</code>
     * @return The version.
     */
    @java.lang.Override
    public int getVersion() {
      return version_;
    }

    public static final int IR_FIELD_NUMBER = 100;
    private ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper ir_;
    /**
     * <code>optional .InternalRequestWrapper ir = 100;</code>
     * @return Whether the ir field is set.
     */
    @java.lang.Override
    public boolean hasIr() {
      return ((bitField0_ & 0x00000008) != 0);
    }
    /**
     * <code>optional .InternalRequestWrapper ir = 100;</code>
     * @return The ir.
     */
    @java.lang.Override
    public ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper getIr() {
      return ir_ == null ? ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper.getDefaultInstance() : ir_;
    }
    /**
     * <code>optional .InternalRequestWrapper ir = 100;</code>
     */
    @java.lang.Override
    public ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapperOrBuilder getIrOrBuilder() {
      return ir_ == null ? ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper.getDefaultInstance() : ir_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (command_ != 0) {
        output.writeUInt32(1, command_);
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        output.writeBytes(2, key_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        output.writeBytes(3, value_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        output.writeInt32(4, version_);
      }
      if (((bitField0_ & 0x00000008) != 0)) {
        output.writeMessage(100, getIr());
      }
      getUnknownFields().writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (command_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, command_);
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, key_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, value_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, version_);
      }
      if (((bitField0_ & 0x00000008) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(100, getIr());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest)) {
        return super.equals(obj);
      }
      ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest other = (ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest) obj;

      if (getCommand()
          != other.getCommand()) return false;
      if (hasKey() != other.hasKey()) return false;
      if (hasKey()) {
        if (!getKey()
            .equals(other.getKey())) return false;
      }
      if (hasValue() != other.hasValue()) return false;
      if (hasValue()) {
        if (!getValue()
            .equals(other.getValue())) return false;
      }
      if (hasVersion() != other.hasVersion()) return false;
      if (hasVersion()) {
        if (getVersion()
            != other.getVersion()) return false;
      }
      if (hasIr() != other.hasIr()) return false;
      if (hasIr()) {
        if (!getIr()
            .equals(other.getIr())) return false;
      }
      if (!getUnknownFields().equals(other.getUnknownFields())) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + COMMAND_FIELD_NUMBER;
      hash = (53 * hash) + getCommand();
      if (hasKey()) {
        hash = (37 * hash) + KEY_FIELD_NUMBER;
        hash = (53 * hash) + getKey().hashCode();
      }
      if (hasValue()) {
        hash = (37 * hash) + VALUE_FIELD_NUMBER;
        hash = (53 * hash) + getValue().hashCode();
      }
      if (hasVersion()) {
        hash = (37 * hash) + VERSION_FIELD_NUMBER;
        hash = (53 * hash) + getVersion();
      }
      if (hasIr()) {
        hash = (37 * hash) + IR_FIELD_NUMBER;
        hash = (53 * hash) + getIr().hashCode();
      }
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
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
     * Protobuf type {@code KVRequest}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:KVRequest)
        ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequestOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ca.NetSysLab.ProtocolBuffers.KeyValueRequest.internal_static_KVRequest_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ca.NetSysLab.ProtocolBuffers.KeyValueRequest.internal_static_KVRequest_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest.class, ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest.Builder.class);
      }

      // Construct using ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest.newBuilder()
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
          getIrFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        command_ = 0;

        key_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        value_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000002);
        version_ = 0;
        bitField0_ = (bitField0_ & ~0x00000004);
        if (irBuilder_ == null) {
          ir_ = null;
        } else {
          irBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000008);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ca.NetSysLab.ProtocolBuffers.KeyValueRequest.internal_static_KVRequest_descriptor;
      }

      @java.lang.Override
      public ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest getDefaultInstanceForType() {
        return ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest.getDefaultInstance();
      }

      @java.lang.Override
      public ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest build() {
        ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest buildPartial() {
        ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest result = new ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        result.command_ = command_;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.key_ = key_;
        if (((from_bitField0_ & 0x00000002) != 0)) {
          to_bitField0_ |= 0x00000002;
        }
        result.value_ = value_;
        if (((from_bitField0_ & 0x00000004) != 0)) {
          result.version_ = version_;
          to_bitField0_ |= 0x00000004;
        }
        if (((from_bitField0_ & 0x00000008) != 0)) {
          if (irBuilder_ == null) {
            result.ir_ = ir_;
          } else {
            result.ir_ = irBuilder_.build();
          }
          to_bitField0_ |= 0x00000008;
        }
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest) {
          return mergeFrom((ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest other) {
        if (other == ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest.getDefaultInstance()) return this;
        if (other.getCommand() != 0) {
          setCommand(other.getCommand());
        }
        if (other.hasKey()) {
          setKey(other.getKey());
        }
        if (other.hasValue()) {
          setValue(other.getValue());
        }
        if (other.hasVersion()) {
          setVersion(other.getVersion());
        }
        if (other.hasIr()) {
          mergeIr(other.getIr());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 8: {
                command_ = input.readUInt32();

                break;
              } // case 8
              case 18: {
                key_ = input.readBytes();
                bitField0_ |= 0x00000001;
                break;
              } // case 18
              case 26: {
                value_ = input.readBytes();
                bitField0_ |= 0x00000002;
                break;
              } // case 26
              case 32: {
                version_ = input.readInt32();
                bitField0_ |= 0x00000004;
                break;
              } // case 32
              case 802: {
                input.readMessage(
                    getIrFieldBuilder().getBuilder(),
                    extensionRegistry);
                bitField0_ |= 0x00000008;
                break;
              } // case 802
              default: {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
            } // switch (tag)
          } // while (!done)
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.unwrapIOException();
        } finally {
          onChanged();
        } // finally
        return this;
      }
      private int bitField0_;

      private int command_ ;
      /**
       * <code>uint32 command = 1;</code>
       * @return The command.
       */
      @java.lang.Override
      public int getCommand() {
        return command_;
      }
      /**
       * <code>uint32 command = 1;</code>
       * @param value The command to set.
       * @return This builder for chaining.
       */
      public Builder setCommand(int value) {
        
        command_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 command = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearCommand() {
        
        command_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString key_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>optional bytes key = 2;</code>
       * @return Whether the key field is set.
       */
      @java.lang.Override
      public boolean hasKey() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>optional bytes key = 2;</code>
       * @return The key.
       */
      @java.lang.Override
      public com.google.protobuf.ByteString getKey() {
        return key_;
      }
      /**
       * <code>optional bytes key = 2;</code>
       * @param value The key to set.
       * @return This builder for chaining.
       */
      public Builder setKey(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        key_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional bytes key = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearKey() {
        bitField0_ = (bitField0_ & ~0x00000001);
        key_ = getDefaultInstance().getKey();
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString value_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>optional bytes value = 3;</code>
       * @return Whether the value field is set.
       */
      @java.lang.Override
      public boolean hasValue() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>optional bytes value = 3;</code>
       * @return The value.
       */
      @java.lang.Override
      public com.google.protobuf.ByteString getValue() {
        return value_;
      }
      /**
       * <code>optional bytes value = 3;</code>
       * @param value The value to set.
       * @return This builder for chaining.
       */
      public Builder setValue(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        value_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional bytes value = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearValue() {
        bitField0_ = (bitField0_ & ~0x00000002);
        value_ = getDefaultInstance().getValue();
        onChanged();
        return this;
      }

      private int version_ ;
      /**
       * <code>optional int32 version = 4;</code>
       * @return Whether the version field is set.
       */
      @java.lang.Override
      public boolean hasVersion() {
        return ((bitField0_ & 0x00000004) != 0);
      }
      /**
       * <code>optional int32 version = 4;</code>
       * @return The version.
       */
      @java.lang.Override
      public int getVersion() {
        return version_;
      }
      /**
       * <code>optional int32 version = 4;</code>
       * @param value The version to set.
       * @return This builder for chaining.
       */
      public Builder setVersion(int value) {
        bitField0_ |= 0x00000004;
        version_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 version = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearVersion() {
        bitField0_ = (bitField0_ & ~0x00000004);
        version_ = 0;
        onChanged();
        return this;
      }

      private ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper ir_;
      private com.google.protobuf.SingleFieldBuilderV3<
          ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper, ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper.Builder, ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapperOrBuilder> irBuilder_;
      /**
       * <code>optional .InternalRequestWrapper ir = 100;</code>
       * @return Whether the ir field is set.
       */
      public boolean hasIr() {
        return ((bitField0_ & 0x00000008) != 0);
      }
      /**
       * <code>optional .InternalRequestWrapper ir = 100;</code>
       * @return The ir.
       */
      public ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper getIr() {
        if (irBuilder_ == null) {
          return ir_ == null ? ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper.getDefaultInstance() : ir_;
        } else {
          return irBuilder_.getMessage();
        }
      }
      /**
       * <code>optional .InternalRequestWrapper ir = 100;</code>
       */
      public Builder setIr(ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper value) {
        if (irBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ir_ = value;
          onChanged();
        } else {
          irBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000008;
        return this;
      }
      /**
       * <code>optional .InternalRequestWrapper ir = 100;</code>
       */
      public Builder setIr(
          ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper.Builder builderForValue) {
        if (irBuilder_ == null) {
          ir_ = builderForValue.build();
          onChanged();
        } else {
          irBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000008;
        return this;
      }
      /**
       * <code>optional .InternalRequestWrapper ir = 100;</code>
       */
      public Builder mergeIr(ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper value) {
        if (irBuilder_ == null) {
          if (((bitField0_ & 0x00000008) != 0) &&
              ir_ != null &&
              ir_ != ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper.getDefaultInstance()) {
            ir_ =
              ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper.newBuilder(ir_).mergeFrom(value).buildPartial();
          } else {
            ir_ = value;
          }
          onChanged();
        } else {
          irBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000008;
        return this;
      }
      /**
       * <code>optional .InternalRequestWrapper ir = 100;</code>
       */
      public Builder clearIr() {
        if (irBuilder_ == null) {
          ir_ = null;
          onChanged();
        } else {
          irBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000008);
        return this;
      }
      /**
       * <code>optional .InternalRequestWrapper ir = 100;</code>
       */
      public ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper.Builder getIrBuilder() {
        bitField0_ |= 0x00000008;
        onChanged();
        return getIrFieldBuilder().getBuilder();
      }
      /**
       * <code>optional .InternalRequestWrapper ir = 100;</code>
       */
      public ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapperOrBuilder getIrOrBuilder() {
        if (irBuilder_ != null) {
          return irBuilder_.getMessageOrBuilder();
        } else {
          return ir_ == null ?
              ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper.getDefaultInstance() : ir_;
        }
      }
      /**
       * <code>optional .InternalRequestWrapper ir = 100;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper, ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper.Builder, ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapperOrBuilder> 
          getIrFieldBuilder() {
        if (irBuilder_ == null) {
          irBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper, ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapper.Builder, ca.NetSysLab.ProtocolBuffers.InternalRequest.InternalRequestWrapperOrBuilder>(
                  getIr(),
                  getParentForChildren(),
                  isClean());
          ir_ = null;
        }
        return irBuilder_;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:KVRequest)
    }

    // @@protoc_insertion_point(class_scope:KVRequest)
    private static final ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest();
    }

    public static ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<KVRequest>
        PARSER = new com.google.protobuf.AbstractParser<KVRequest>() {
      @java.lang.Override
      public KVRequest parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        Builder builder = newBuilder();
        try {
          builder.mergeFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(builder.buildPartial());
        } catch (com.google.protobuf.UninitializedMessageException e) {
          throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(e)
              .setUnfinishedMessage(builder.buildPartial());
        }
        return builder.buildPartial();
      }
    };

    public static com.google.protobuf.Parser<KVRequest> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<KVRequest> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_KVRequest_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_KVRequest_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\"src/protobuf/KeyValueRequest.proto\032\"sr" +
      "c/protobuf/InternalRequest.proto\"\247\001\n\tKVR" +
      "equest\022\017\n\007command\030\001 \001(\r\022\020\n\003key\030\002 \001(\014H\000\210\001" +
      "\001\022\022\n\005value\030\003 \001(\014H\001\210\001\001\022\024\n\007version\030\004 \001(\005H\002" +
      "\210\001\001\022(\n\002ir\030d \001(\0132\027.InternalRequestWrapper" +
      "H\003\210\001\001B\006\n\004_keyB\010\n\006_valueB\n\n\010_versionB\005\n\003_" +
      "irB/\n\034ca.NetSysLab.ProtocolBuffersB\017KeyV" +
      "alueRequestb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          ca.NetSysLab.ProtocolBuffers.InternalRequest.getDescriptor(),
        });
    internal_static_KVRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_KVRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_KVRequest_descriptor,
        new java.lang.String[] { "Command", "Key", "Value", "Version", "Ir", "Key", "Value", "Version", "Ir", });
    ca.NetSysLab.ProtocolBuffers.InternalRequest.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
