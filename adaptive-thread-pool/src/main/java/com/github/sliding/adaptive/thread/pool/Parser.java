package com.github.sliding.adaptive.thread.pool;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Parser template for reading and writing to/from a stream
 */
public interface Parser {
    String read(Charset charset) throws IOException;

    String read() throws IOException;

    void write(String content, Charset charset) throws IOException;

    void write(String content) throws IOException;

}
