/*
 * Copyright 2018-2019 EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.ac.ebi.ait.filecontentvalidatordemo.utils;

import uk.ac.ebi.ait.filecontentvalidatordemo.error.WebinCliException;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileUtils {

//	public static BufferedReader
//	getBufferedReader( File file ) throws IOException
//	{
//		if( file.getName().matches( "^.+\\.gz$" ) || file.getName().matches( "^.+\\.gzip$" ) )
//		{
//			GZIPInputStream gzip = new GZIPInputStream( new FileInputStream( file ) );
//			return new BufferedReader( new InputStreamReader( gzip ) );
//
//		} else if( file.getName().matches( "^.+\\.bz2$" ) || file.getName().matches( "^.+\\.bzip2$" ) )
//		{
//			BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream( new FileInputStream( file ) );
//			return new BufferedReader( new InputStreamReader( bzIn ) );
//
//		} else
//		{
//			return new BufferedReader( new FileReader(file ) );
//		}
//	}
//
//    static public String
//    calculateDigest(String digestName, File file )
//    {
//    	try
//		{
//			MessageDigest digest = MessageDigest.getInstance( digestName );
//			byte[] buf = new byte[ 4096 ];
//			int  read = 0;
//			try( BufferedInputStream is = new BufferedInputStream( new FileInputStream( file ) ) )
//			{
//				while( ( read = is.read( buf ) ) > 0 )
//					digest.update( buf, 0, read );
//
//				byte[] message_digest = digest.digest();
//				BigInteger value = new BigInteger( 1, message_digest );
//				return String.format( String.format( "%%0%dx", message_digest.length << 1 ), value );
//			}
//		} catch( NoSuchAlgorithmException | IOException ex )
//		{
//			throw WebinCliException.systemError( ex );
//		}
//    }

	// Directory creation.
	public static File
	getReportFile(File dir, String filename, String suffix) {
		if (dir == null || !dir.isDirectory())
			throw WebinCliException.systemError(WebinCliMessage.CLI_INVALID_REPORT_DIR_ERROR.format(filename));

		return new File(dir, Paths.get(filename).getFileName().toString() + suffix);
	}

	public static boolean 
	emptyDirectory( File dir )
	{
		if (dir == null)
			return false;
	    if( dir.exists() )
	    {
	        File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					emptyDirectory(file);
				} else {
					file.delete();
				}
			}
	    }
	    return dir.listFiles().length == 0;
	}

	public static File
	createOutputDir(File outputDir, String... dirs) throws WebinCliException {
		if (outputDir == null) {
			throw WebinCliException.systemError(WebinCliMessage.CLI_MISSING_OUTPUT_DIR_ERROR.text());
		}

		String[] safeDirs = getSafeOutputDirs(dirs);

		Path p;

		try {
			p = Paths.get(outputDir.getPath(), safeDirs);
		} catch (InvalidPathException ex) {
			throw WebinCliException.systemError(WebinCliMessage.CLI_CREATE_DIR_ERROR.format(ex.getInput()));
		}

		File dir = p.toFile();

		if (!dir.exists() && !dir.mkdirs()) {
			throw WebinCliException.systemError(WebinCliMessage.CLI_CREATE_DIR_ERROR.format(dir.getPath()));
		}

		return dir;
	}

	private static String[]
	getSafeOutputDirs(String... dirs) {
		return Arrays.stream(dirs)
				.map(FileUtils::getSafeOutputDir)
				.toArray(String[]::new);
	}

	private static String
	getSafeOutputDir(String dir) {
		return dir
				.replaceAll("[^a-zA-Z0-9-_\\.]", "_")
				.replaceAll("_+", "_")
				.replaceAll("^_+(?=[^_])", "")
				.replaceAll("(?<=[^_])_+$", "");
	}

}
