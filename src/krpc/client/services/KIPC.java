package krpc.client.services;

import com.google.protobuf.ByteString;
import krpc.client.*;

import java.io.IOException;

public class KIPC {

	private Connection _connection;

	private KIPC(Connection connection) {
		this._connection = connection;
	}

	public static KIPC newInstance(Connection connection) {
		return new KIPC(connection);
	}

	@SuppressWarnings({"unchecked"})
	@RPCInfo(service = "KIPC", procedure = "GetMessages", returnTypeSpec = _ReturnTypeSpec.class)
	public java.util.List<String> getMessages() throws RPCException, IOException {
		ByteString _data = this._connection.invoke("KIPC", "GetMessages");
		return (java.util.List<String>) Encoder.decode(_data, new TypeSpecification(java.util.List.class, new TypeSpecification(String.class)), this._connection);
	}

	@SuppressWarnings({"unchecked"})
	@RPCInfo(service = "KIPC", procedure = "GetPartsTagged", returnTypeSpec = _ReturnTypeSpec.class)
	public java.util.List<krpc.client.services.SpaceCenter.Part> getPartsTagged(krpc.client.services.SpaceCenter.Vessel vessel, String tag) throws RPCException, IOException {
		ByteString[] _args = new ByteString[]{
				Encoder.encode(vessel),
				Encoder.encode(tag)
		};
		ByteString _data = this._connection.invoke("KIPC", "GetPartsTagged", _args);
		return (java.util.List<krpc.client.services.SpaceCenter.Part>) Encoder.decode(_data, new TypeSpecification(java.util.List.class, new TypeSpecification(krpc.client.services.SpaceCenter.Part.class)), this._connection);
	}

	@SuppressWarnings({"unchecked"})
	@RPCInfo(service = "KIPC", procedure = "GetProcessor", returnTypeSpec = _ReturnTypeSpec.class)
	public java.util.List<krpc.client.services.KIPC.Processor> getProcessor(krpc.client.services.SpaceCenter.Part part) throws RPCException, IOException {
		ByteString[] _args = new ByteString[]{
				Encoder.encode(part)
		};
		ByteString _data = this._connection.invoke("KIPC", "GetProcessor", _args);
		return (java.util.List<krpc.client.services.KIPC.Processor>) Encoder.decode(_data, new TypeSpecification(java.util.List.class, new TypeSpecification(krpc.client.services.KIPC.Processor.class)), this._connection);
	}

	@SuppressWarnings({"unchecked"})
	@RPCInfo(service = "KIPC", procedure = "GetProcessors", returnTypeSpec = _ReturnTypeSpec.class)
	public java.util.List<krpc.client.services.KIPC.Processor> getProcessors(krpc.client.services.SpaceCenter.Vessel vessel) throws RPCException, IOException {
		ByteString[] _args = new ByteString[]{
				Encoder.encode(vessel)
		};
		ByteString _data = this._connection.invoke("KIPC", "GetProcessors", _args);
		return (java.util.List<krpc.client.services.KIPC.Processor>) Encoder.decode(_data, new TypeSpecification(java.util.List.class, new TypeSpecification(krpc.client.services.KIPC.Processor.class)), this._connection);
	}

	@SuppressWarnings({"unchecked"})
	@RPCInfo(service = "KIPC", procedure = "PeekMessage", returnTypeSpec = _ReturnTypeSpec.class)
	public String peekMessage() throws RPCException, IOException {
		ByteString _data = this._connection.invoke("KIPC", "PeekMessage");
		return (String) Encoder.decode(_data, new TypeSpecification(String.class), this._connection);
	}

	@SuppressWarnings({"unchecked"})
	@RPCInfo(service = "KIPC", procedure = "PopMessage", returnTypeSpec = _ReturnTypeSpec.class)
	public String popMessage() throws RPCException, IOException {
		ByteString _data = this._connection.invoke("KIPC", "PopMessage");
		return (String) Encoder.decode(_data, new TypeSpecification(String.class), this._connection);
	}

	@SuppressWarnings({"unchecked"})
	@RPCInfo(service = "KIPC", procedure = "ResolveBodies", returnTypeSpec = _ReturnTypeSpec.class)
	public java.util.List<krpc.client.services.SpaceCenter.CelestialBody> resolveBodies(java.util.List<Integer> bodyIds) throws RPCException, IOException {
		ByteString[] _args = new ByteString[]{
				Encoder.encode(bodyIds)
		};
		ByteString _data = this._connection.invoke("KIPC", "ResolveBodies", _args);
		return (java.util.List<krpc.client.services.SpaceCenter.CelestialBody>) Encoder.decode(_data, new TypeSpecification(java.util.List.class, new TypeSpecification(krpc.client.services.SpaceCenter.CelestialBody.class)), this._connection);
	}

	@SuppressWarnings({"unchecked"})
	@RPCInfo(service = "KIPC", procedure = "ResolveBody", returnTypeSpec = _ReturnTypeSpec.class)
	public krpc.client.services.SpaceCenter.CelestialBody resolveBody(int bodyId) throws RPCException, IOException {
		ByteString[] _args = new ByteString[]{
				Encoder.encode(bodyId)
		};
		ByteString _data = this._connection.invoke("KIPC", "ResolveBody", _args);
		return (krpc.client.services.SpaceCenter.CelestialBody) Encoder.decode(_data, new TypeSpecification(krpc.client.services.SpaceCenter.CelestialBody.class), this._connection);
	}

	@SuppressWarnings({"unchecked"})
	@RPCInfo(service = "KIPC", procedure = "ResolveVessel", returnTypeSpec = _ReturnTypeSpec.class)
	public krpc.client.services.SpaceCenter.Vessel resolveVessel(String vesselGuid) throws RPCException, IOException {
		ByteString[] _args = new ByteString[]{
				Encoder.encode(vesselGuid)
		};
		ByteString _data = this._connection.invoke("KIPC", "ResolveVessel", _args);
		return (krpc.client.services.SpaceCenter.Vessel) Encoder.decode(_data, new TypeSpecification(krpc.client.services.SpaceCenter.Vessel.class), this._connection);
	}

	@SuppressWarnings({"unchecked"})
	@RPCInfo(service = "KIPC", procedure = "ResolveVessels", returnTypeSpec = _ReturnTypeSpec.class)
	public java.util.List<krpc.client.services.SpaceCenter.Vessel> resolveVessels(java.util.List<String> vesselGuids) throws RPCException, IOException {
		ByteString[] _args = new ByteString[]{
				Encoder.encode(vesselGuids)
		};
		ByteString _data = this._connection.invoke("KIPC", "ResolveVessels", _args);
		return (java.util.List<krpc.client.services.SpaceCenter.Vessel>) Encoder.decode(_data, new TypeSpecification(java.util.List.class, new TypeSpecification(krpc.client.services.SpaceCenter.Vessel.class)), this._connection);
	}

	@SuppressWarnings({"unchecked"})
	@RPCInfo(service = "KIPC", procedure = "get_CountMessages", returnTypeSpec = _ReturnTypeSpec.class)
	public int getCountMessages() throws RPCException, IOException {
		ByteString _data = this._connection.invoke("KIPC", "get_CountMessages");
		return (int) Encoder.decode(_data, new TypeSpecification(Integer.class), this._connection);
	}

	public static class _ReturnTypeSpec {
		public static TypeSpecification get(String procedure) {
			switch (procedure) {
				case "GetMessages":
					return new TypeSpecification(java.util.List.class, new TypeSpecification(String.class));
				case "GetPartsTagged":
					return new TypeSpecification(java.util.List.class, new TypeSpecification(krpc.client.services.SpaceCenter.Part.class));
				case "GetProcessor":
					return new TypeSpecification(java.util.List.class, new TypeSpecification(krpc.client.services.KIPC.Processor.class));
				case "GetProcessors":
					return new TypeSpecification(java.util.List.class, new TypeSpecification(krpc.client.services.KIPC.Processor.class));
				case "PeekMessage":
					return new TypeSpecification(String.class);
				case "PopMessage":
					return new TypeSpecification(String.class);
				case "ResolveBodies":
					return new TypeSpecification(java.util.List.class, new TypeSpecification(krpc.client.services.SpaceCenter.CelestialBody.class));
				case "ResolveBody":
					return new TypeSpecification(krpc.client.services.SpaceCenter.CelestialBody.class);
				case "ResolveVessel":
					return new TypeSpecification(krpc.client.services.SpaceCenter.Vessel.class);
				case "ResolveVessels":
					return new TypeSpecification(java.util.List.class, new TypeSpecification(krpc.client.services.SpaceCenter.Vessel.class));
				case "get_CountMessages":
					return new TypeSpecification(Integer.class);
				case "Processor_SendMessage":
					return new TypeSpecification(Boolean.class);
				case "Processor_get_TerminalVisible":
					return new TypeSpecification(Boolean.class);
				case "Processor_get_Part":
					return new TypeSpecification(krpc.client.services.SpaceCenter.Part.class);
				case "Processor_get_Powered":
					return new TypeSpecification(Boolean.class);
				case "Processor_get_DiskSpace":
					return new TypeSpecification(Integer.class);
			}
			throw new IllegalArgumentException("No type specification found for '" + procedure + "'");
		}
	}

	public static class Processor extends RemoteObject {

		private static final long serialVersionUID = 380322855591807944L;

		public Processor(Connection connection, long id) {
			super(connection, id);
		}


		@SuppressWarnings({"unchecked"})
		@RPCInfo(service = "KIPC", procedure = "Processor_SendMessage", returnTypeSpec = _ReturnTypeSpec.class)
		public boolean sendMessage(String json) throws RPCException, IOException {
			ByteString[] _args = new ByteString[]{
					Encoder.encode(this),
					Encoder.encode(json)
			};
			ByteString _data = this._connection.invoke("KIPC", "Processor_SendMessage", _args);
			return (boolean) Encoder.decode(_data, new TypeSpecification(Boolean.class), this._connection);
		}


		@SuppressWarnings({"unchecked"})
		@RPCInfo(service = "KIPC", procedure = "Processor_get_TerminalVisible", returnTypeSpec = _ReturnTypeSpec.class)
		public boolean getTerminalVisible() throws RPCException, IOException {
			ByteString[] _args = new ByteString[]{
					Encoder.encode(this)
			};
			ByteString _data = this._connection.invoke("KIPC", "Processor_get_TerminalVisible", _args);
			return (boolean) Encoder.decode(_data, new TypeSpecification(Boolean.class), this._connection);
		}

		@SuppressWarnings({"unchecked"})
		@RPCInfo(service = "KIPC", procedure = "Processor_set_TerminalVisible", returnTypeSpec = _ReturnTypeSpec.class)
		public void setTerminalVisible(boolean value) throws RPCException, IOException {
			ByteString[] _args = new ByteString[]{
					Encoder.encode(this),
					Encoder.encode(value)
			};
			this._connection.invoke("KIPC", "Processor_set_TerminalVisible", _args);
		}

		@SuppressWarnings({"unchecked"})
		@RPCInfo(service = "KIPC", procedure = "Processor_get_Part", returnTypeSpec = _ReturnTypeSpec.class)
		public krpc.client.services.SpaceCenter.Part getPart() throws RPCException, IOException {
			ByteString[] _args = new ByteString[]{
					Encoder.encode(this)
			};
			ByteString _data = this._connection.invoke("KIPC", "Processor_get_Part", _args);
			return (krpc.client.services.SpaceCenter.Part) Encoder.decode(_data, new TypeSpecification(krpc.client.services.SpaceCenter.Part.class), this._connection);
		}


		@SuppressWarnings({"unchecked"})
		@RPCInfo(service = "KIPC", procedure = "Processor_get_Powered", returnTypeSpec = _ReturnTypeSpec.class)
		public boolean getPowered() throws RPCException, IOException {
			ByteString[] _args = new ByteString[]{
					Encoder.encode(this)
			};
			ByteString _data = this._connection.invoke("KIPC", "Processor_get_Powered", _args);
			return (boolean) Encoder.decode(_data, new TypeSpecification(Boolean.class), this._connection);
		}

		@SuppressWarnings({"unchecked"})
		@RPCInfo(service = "KIPC", procedure = "Processor_set_Powered", returnTypeSpec = _ReturnTypeSpec.class)
		public void setPowered(boolean value) throws RPCException, IOException {
			ByteString[] _args = new ByteString[]{
					Encoder.encode(this),
					Encoder.encode(value)
			};
			this._connection.invoke("KIPC", "Processor_set_Powered", _args);
		}

		@SuppressWarnings({"unchecked"})
		@RPCInfo(service = "KIPC", procedure = "Processor_get_DiskSpace", returnTypeSpec = _ReturnTypeSpec.class)
		public int getDiskSpace() throws RPCException, IOException {
			ByteString[] _args = new ByteString[]{
					Encoder.encode(this)
			};
			ByteString _data = this._connection.invoke("KIPC", "Processor_get_DiskSpace", _args);
			return (int) Encoder.decode(_data, new TypeSpecification(Integer.class), this._connection);
		}


	}


}

