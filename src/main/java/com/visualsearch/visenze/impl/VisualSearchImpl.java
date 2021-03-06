/*
 * Created on 2017-nov-15
 *
 * (c) 2017, Visual Search
 */
package com.visualsearch.visenze.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.visenze.visearch.Image;
import com.visenze.visearch.ImageResult;
import com.visenze.visearch.PagedSearchResult;
import com.visenze.visearch.SearchParams;
import com.visenze.visearch.UploadSearchParams;
import com.visenze.visearch.ViSearch;
import com.visualsearch.beans.ConfigBean;
import com.visualsearch.config.impl.PropValueHandler;
import com.visualsearch.filehandling.impl.Base64DataHandler;

/**
 * 
 * @author avsin3
 *
 */
public class VisualSearchImpl {
	
	
	private static ConfigBean bean;
	private static ViSearch client;
	
	public VisualSearchImpl() {
		bean = new PropValueHandler().valueHandler();
		client = new ViSearch(bean.getAccessKey(), bean.getSecretKey());
	}
		
	
		
		//private static ViSearch client_POCApplication = new ViSearch("2922ca9709bb6f648b3cc0c95dd25453", "5ff65d3b5b8f5262c82e5fb4b6c1cc79");
		
		
	/**
	 * This will search the similar images for given image.
	 * @return String
	 * @throws IOException 
	 */
	public String searchByImage (final String imageUrl) throws IOException {
		
		File outputfile = Base64DataHandler.decodeToImage(imageUrl);
		
		UploadSearchParams searchParamas = new UploadSearchParams(outputfile);
		
		searchParamas.setLimit(bean.getSearchlimit());
		searchParamas.setPage(1);
		searchParamas.setScore(bean.isScore());
		searchParamas.setScoreMin(bean.getScoreMin());
		searchParamas.setScoreMax(bean.getScoreMax());
		searchParamas.setFacet(bean.isFacet());		
		
		searchParamas.setGetAllFl(bean.isAllFl()); // to get all the image URLS
		searchParamas.setQInfo(bean.isQInfo());// To get the main image URL
		
		
		PagedSearchResult searchResult = client.uploadSearch(searchParamas);
		System.out.println("Search data list  - " + searchResult.getRawJson());
		String errorMessage = searchResult.getErrorMessage();
		if (errorMessage != null) {
			return errorMessage;
		}
				
		
		//get imid from previous request
		//String imId = searchResult.getImId();

		//UploadSearchParams param = new UploadSearchParams(imId);
		//PagedSearchResult result = client_VisualSearchPOC.uploadSearch(param);
		
		
		//System.out.println("Search data list - " + result.getRawJson());
		
		return searchResult.getRawJson();
	}
	

	/**
	 * 
	 * @param this will search the similar images by "im_name"
	 * @return
	 */
	public String searchByName(String im_name) {

		// Set the required filters.
		SearchParams params = setFiltersForSimilarRecommendations(im_name);

		PagedSearchResult searchResult = client.search(params);

		String errorMessage = searchResult.getErrorMessage();
		if (errorMessage != null) {
			return errorMessage;
		}

		System.out.println("Search data list - " + searchResult.getRawJson());
		// System.out.println("Search image result. -
		// "+searchResult.getResult());

		List<ImageResult> result = searchResult.getResult();
		for (ImageResult imageResult : result) {
			ImageResult ir = imageResult;
			// System.out.println(ir.getImName());
		}

		return searchResult.getRawJson();
	}
	
	
	
	/**
	 * This will search the image with URL
	 * @param url
	 * @return
	 */
	public String searchByUrl(String url) {
		// Searching an uploaded image file
		File imageFile = new File("C:/Users/Public/Pictures/Sample Pictures/Penguins.jpg");
		//String url = "http://www.ikea.com/us/en/images/products/billy-bookcase-white__0252367_PE391149_S4.JPG";
		UploadSearchParams params = setFiltersForSearchByUploadedImageAndURL(url);

		// params.setDetection(detection)
		// System.out.println(params.getImageFile());
		// PagedSearchResult searchResult = client.uploadSearch(params);
		// if(searchResult.getErrorMessage()!= null) {
		// return searchResult.getErrorMessage();
		// }
		// System.out.println(searchResult.getRawJson());

		// Searching a publicly accessible image URL

		PagedSearchResult searchResult2 = client.uploadSearch(params);
		System.out.println(searchResult2.getRawJson());
		System.out.println(searchResult2.getErrorMessage());

		return "";
	}

	/**
	 * @param url
	 * @return
	 */
	private UploadSearchParams setFiltersForSearchByUploadedImageAndURL(String url) {
		UploadSearchParams params = new UploadSearchParams(url);
		params.setFacet(true);
		params.setGetAllFl(true);
		params.setLimit(30);
		params.setPage(1);
		params.setScore(true);
		params.setQInfo(true);

		List<String> fl = new ArrayList<String>();
		fl.add(url);
		params.setFl(fl);
		return params;
	}

	/**
	 * @param requestData
	 * @return
	 */
	private SearchParams setFiltersForSimilarRecommendations(String requestData) {

		SearchParams params = new SearchParams(requestData);

		List<String> fl = new ArrayList<String>();
		fl.add(requestData);

		// SearchParams params = new SearchParams("malm2");
		params.setLimit(30);
		params.setPage(1);
		params.setScore(true);
		//params.setScoreMin(bean.getScoreMin());
		//params.setScoreMax(bean.getScoreMax());
		params.setFacet(true);
		params.setFl(fl);
		params.setGetAllFl(true); // to get all the image URLS
		params.setQInfo(true);// To get the main image URL
		return params;
	}

	/*public static void main(String args[]) {
		VisualSearchImpl impl = new VisualSearchImpl();
		//impl.searchForSimilarRecommendations("malm100");
		//String url = "http://www.ikea.com/us/en/images/products/billy-bookcase-white__0252367_PE391149_S4.JPG";
		//impl.searchByUploadedImageAndURL(url);
		impl.searchByImage("C:/Users/Public/Pictures/Sample Pictures/Penguins.jpg");
	}*/

	/**
	 * 
	 * @param uploadSearchParams
	 * @return
	 */
	private PagedSearchResult uploadSearchImages(UploadSearchParams uploadSearchParams) {


		List<Image> images = new ArrayList<Image>();
		String imName = "new_upload_IKEA2";
		String imUrl = "http://www.ikea.com/ms/media/logos/diners.gif";

		// add metadata to your image
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("category", "IKEA");
		// metadata.put("description", "A pair of high quality leather
		// wingtips");
		// metadata.put("price", "100.0");
		images.add(new Image(imName, imUrl, metadata));
		client.insert(images);

		return null;
	}

	
	

	
}
