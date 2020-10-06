package org.glassfish.json;

import org.glassfish.json.api.BufferPool;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

public class WriterAccess extends JsonWriterImpl {
	public WriterAccess(Writer writer, BufferPool bufferPool) {
		super(writer, bufferPool);
	}

	public WriterAccess(Writer writer, boolean prettyPrinting, BufferPool bufferPool) {
		super(writer, prettyPrinting, bufferPool);
	}

	public WriterAccess(OutputStream out, BufferPool bufferPool) {
		super(out, bufferPool);
	}

	public WriterAccess(OutputStream out, boolean prettyPrinting, BufferPool bufferPool) {
		super(out, prettyPrinting, bufferPool);
	}

	public WriterAccess(OutputStream out, Charset charset, boolean prettyPrinting, BufferPool bufferPool) {
		super(out, charset, prettyPrinting, bufferPool);
	}
}
