package firok.spring.geo;

import net.redelsoft.gisconverter.core.ConvertProcess;
import net.redelsoft.gisconverter.core.type.ShapeFile;
import net.redelsoft.gisconverter.ext.FileExtension;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.PathParam;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/convert")
public class ConvertController
{
	private Map<String, File> mappingResults = new HashMap<>();

	@PostMapping("/toSHP")
	public String toSHP(
			@RequestParam(name = "filename") final String filenameGeojson,
			@RequestBody final String geojson
	) throws Exception
	{
//		System.out.println("文件名"+ fileGeojsonName);
//		System.out.println("文件内容");
//		System.out.println(geojson);

		UUID uuid = UUID.randomUUID(); // 本次执行接口的唯一id
		String uuidString = uuid.toString();
		Path pathDirTemp = Paths.get(".","temp",uuidString);
		File fileDirTemp = pathDirTemp.toFile();

		// 创建缓存文件夹
		boolean resultMkdirs = fileDirTemp.mkdirs();
		if(!resultMkdirs) throw new IOException("创建缓存文件夹失败");

		// 创建缓存文件
		File fileDataGeojson = new File(fileDirTemp, filenameGeojson);
		try { FileUtils.writeStringToFile(fileDataGeojson, geojson, StandardCharsets.UTF_8); }
		catch (Exception e) { throw new IOException("创建缓存文件失败"); }
		File fileOutZip = new File(fileDirTemp, filenameGeojson + ".zip");

		// 创建转换器
		FileExtension fe = new FileExtension("ESRI Shape File (.shp)", "shp", new ShapeFile());
		ConvertProcess process = new ConvertProcess(fileDataGeojson, fe);
		process.execute();

		File[] listFiles = Optional.ofNullable(fileDirTemp.listFiles((dir, name) -> !name.equals(filenameGeojson))).orElse(new File[0]);
		if(listFiles.length == 0) throw new IOException("无法读取转换结果");
		ZipUtil.toZip(listFiles, fileOutZip, false);

		mappingResults.put(uuidString, fileOutZip);

		return uuidString;
	}

	@GetMapping(value = "/download/{uuid}",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public FileSystemResource download(
			@PathVariable String uuid,
			HttpServletResponse response
	) throws UnsupportedEncodingException
	{
		File file = mappingResults.get(uuid);
		String filename = URLEncoder.encode(file.getName(), "UTF-8");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,"filename=\""+filename+"\"");
		return new FileSystemResource(file);
	}

//	static void test(String fileGeojsonName, File fileDirTemp)
//	{
//		ObjectMapper om = new ObjectMapper();
//		ArrayNode ret = om.createArrayNode();
//
//		// 读取转换之后的文件列表
//		// 把所有数据转换成Base64字符串返给前台
//		File[] arrFiles = Optional.ofNullable(fileDirTemp.listFiles()).orElse(new File[0]);
//
//		Arrays.stream(arrFiles).parallel().forEach(fileResult -> {
//
//			try
//			{
//				String filename = fileResult.getName();
//				if(filename.equals(fileGeojsonName)) return;
//
//				Base64.Encoder b64encoder = Base64.getEncoder();
//				byte[] filebytes = FileUtils.readFileToByteArray(fileResult);
//				String file64 = b64encoder.encodeToString(filebytes);
//				ObjectNode nodeResult;
//				synchronized (om)
//				{
//					nodeResult = om.createObjectNode();
//					ret.add(nodeResult);
//				}
//
//				nodeResult.put("filename", filename);
//				nodeResult.put("b64", file64);
//				nodeResult.put("filesize", fileResult.length());
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//				throw new RuntimeException(e);
//			}
//		});
//
//		// 删除缓存文件夹 fixme 递归删除
//		fileDirTemp.delete();
//	}
}
