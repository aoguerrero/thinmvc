package com.pagestags.thinmvc.utl;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.pagestags.thinmvc.excp.ServiceException;

public class FileSystemUtils {

	private static final String CLASSPATH_PREFIX = "classpath://";
	private static final String FILE_PREFIX = "file://";
	
	private FileSystemUtils() {
	}

	public static byte[] getContent(String path) {
		if (path.startsWith(CLASSPATH_PREFIX)) {
			path = path.replace(CLASSPATH_PREFIX, "");
			return getClasspathContent(path);
		} else if (path.startsWith(FILE_PREFIX)) {
			path = path.replace(FILE_PREFIX, "");
			return getFileContent(Paths.get(path));
		}
		throw new ServiceException.InternalServer(new Exception("Can't load content from " + path));
	}
	
	public static String getDirectory(String path) {
		String directory = path.replace(CLASSPATH_PREFIX, "").replace(FILE_PREFIX, "");
		return Paths.get(directory).getParent().toString();
	}

	public static boolean isClasspath(String path) {
		return path.startsWith(CLASSPATH_PREFIX);
	}
	
	public static byte[] getClasspathContent(String path) {
		try {
			return HttpUtils.class.getResourceAsStream(path).readAllBytes();
		} catch (IOException e) {
			throw new ServiceException.InternalServer(e);
		}
	}

	public static byte[] getFileContent(Path path) {
		try {
			if(!Files.exists(path)) {
				throw new ServiceException.NotFound();
			}
			return Files.readAllBytes(path);
		} catch (IOException e) {
			throw new ServiceException.InternalServer(e);
		}
	}

	public static void writeStringToFile(Path path, String content) {
		try {
			Files.writeString(path, content, StandardCharsets.UTF_8,
					Files.exists(path) ? TRUNCATE_EXISTING : CREATE_NEW);
		} catch (IOException e) {
			throw new ServiceException.InternalServer(e);
		}
	}
}
