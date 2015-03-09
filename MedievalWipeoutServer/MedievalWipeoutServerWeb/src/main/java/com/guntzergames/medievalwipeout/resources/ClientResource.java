package com.guntzergames.medievalwipeout.resources;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.io.IOUtils;

import com.guntzergames.medievalwipeout.utils.VersionUtils;

@Stateless
@Path("/client")
public class ClientResource {
	
	@GET
	@Path("version")
	@Produces("text/plain")
	public String getVersion() throws IOException {

		return VersionUtils.getVersion("client/version");

	}
	
	@GET
	@Path("package")
	@Produces("application/vnd.android.package-archive")
	public byte[] getPackage() throws IOException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = new FileInputStream("client/MedievalWipeoutClient.apk");
		
		IOUtils.copy(in, out);
		in.close();
		
		return out.toByteArray();
		
	}

}
