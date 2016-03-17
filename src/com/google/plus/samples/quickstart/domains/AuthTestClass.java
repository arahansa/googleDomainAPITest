package com.google.plus.samples.quickstart.domains;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.plusDomains.PlusDomains;
import com.google.api.services.plusDomains.model.Acl;
import com.google.api.services.plusDomains.model.Activity;
import com.google.api.services.plusDomains.model.PlusDomainsAclentryResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class AuthTestClass {

	// List the scopes your app requires:
	private static List<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/plus.me",
			"https://www.googleapis.com/auth/plus.stream.write", "https://www.googleapis.com/auth/plus.stream.read"
			,"https://www.googleapis.com/auth/plus.circles.read", "https://www.googleapis.com/auth/plus.circles.write");

	// The following redirect URI causes Google to return a code to the user's
	// browser that they then manually provide to your app to complete the
	// OAuth flow.
	private static final String REDIRECT_URI = "http://arahansa.com/common/userLoginByGoogleDomain";
	private static final String CLIENT_ID = "121957370597-n5vmeedhrv0545r3n3rpl989vfguobp7.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "ZFcyc3-vj50j4Nu7bLC9EI8x";

	public static void main(String[] args) throws IOException {

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(),
				new JacksonFactory(), CLIENT_ID, // This comes from your
													// Developers Console
													// project
				CLIENT_SECRET, // This, as well
				SCOPE).setApprovalPrompt("force")
						// Set the access type to offline so that the token can
						// be refreshed.
						// By default, the library will automatically refresh
						// tokens when it
						// can, but this can be turned off by setting
						// dfp.api.refreshOAuth2Token=false in your
						// ads.properties file.
						.setAccessType("offline").build();

		// This command-line server-side flow example requires the user to open
		// the
		// authentication URL in their browser to complete the process. In most
		// cases, your app will use a browser-based server-side flow and your
		// user will not need to copy and paste the authorization code. In this
		// type of app, you would be able to skip the next 5 lines.
		// You can also look at the client-side and one-time-code flows for
		// other
		// options at https://developers.google.com/+/web/signin/
		String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
		System.out.println("Please open the following URL in your browser then " + "type the authorization code:");
		System.out.println("  " + url);
		System.out.println("::: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String code = br.readLine();
		// End of command line prompt for the authorization code.

		GoogleTokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
		GoogleCredential credential = new GoogleCredential.Builder().setTransport(new NetHttpTransport())
				.setJsonFactory(new JacksonFactory()).setClientSecrets(CLIENT_ID, CLIENT_SECRET)
				.addRefreshListener(new CredentialRefreshListener() {
					@Override
					public void onTokenResponse(Credential credential, TokenResponse tokenResponse) {
						// Handle success.
						System.out.println("Credential was refreshed successfully.");
						System.out.println("Token Server URL : "+credential.getTokenServerEncodedUrl());
					}

					@Override
					public void onTokenErrorResponse(Credential credential, TokenErrorResponse tokenErrorResponse) {
						// Handle error.
						System.err.println("Credential was not refreshed successfully. "
								+ "Redirect to error page or login screen.");
					}
				})
				// You can also add a credential store listener to have
				// credentials
				// stored automatically.
				// .addRefreshListener(new
				// CredentialStoreRefreshListener(userId, credentialStore))
				.build();

		// Set authorized credentials.
		credential.setFromTokenResponse(tokenResponse);
		System.out.println("credential account id " +credential.getServiceAccountId());
		System.out.println("credential service account user " +credential.getServiceAccountUser());
		System.out.println("Token Info : "+tokenResponse);
		System.out.println("Access Token :: "+ tokenResponse.getAccessToken());
		// Though not necessary when first created, you can manually refresh the
		// token, which is needed after 60 minutes.
		credential.refreshToken();

		// Create a new authorized API client
		PlusDomains plusDomains = new PlusDomains.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName("Winvention")
				.build();
		
		//
		//  Write
		//
		String msg = "Happy Monday! #caseofthemondays";

		// Create a list of ACL entries
		PlusDomainsAclentryResource resource = new PlusDomainsAclentryResource();
		resource.setType("domain"); // Share to domain

		List<PlusDomainsAclentryResource> aclEntries =
		    new ArrayList<PlusDomainsAclentryResource>();
		aclEntries.add(resource);

		Acl acl = new Acl();
		acl.setItems(aclEntries);
		acl.setDomainRestricted(true);  // Required, this does the domain restriction

		// Create a new activity object
		Activity activity = new Activity()
		    .setObject(new Activity.PlusDomainsObject().setOriginalContent(msg))
		    .setAccess(acl);

		// Execute the API request, which calls `activities.insert` for the logged in user
		// activity = plusDomains.activities().insert("me", activity).execute();
		
		// Write Test 2 
		postArticle("OAuth"+tokenResponse.getAccessToken());
		postArticle(" "+tokenResponse.getAccessToken());
		postArticle("OAuth$"+tokenResponse.getAccessToken());
		
		/*
		postArticle("OAuth$"+tokenResponse.getIdToken());
		postArticle("OAuth"+tokenResponse.getIdToken());
		postArticle(tokenResponse.getIdToken());
		*/
	}
	
	private static void postArticle(String auth){
		System.out.println("Auth : "+auth);
		String url3 = "https://www.googleapis.com/plusDomains/v1/people/116596176592415175958/activities";
		URL obj;
		try {
			obj = new URL(url3);
		
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Authorization", auth);
		con.setRequestProperty("Content-Type", "application/json");

		String urlParameters = "{"
				+ "\"object\": "
					+ "{\"originalContent\": \"tting new coversheets on all the TPS reports before they go out now.\"},"
				+ "\"access\":"
					+ "{\"domainRestricted\": true}"
				+ "}";
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.write(urlParameters.getBytes("UTF-8"));
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		String responseMessage = con.getResponseMessage();
		
		
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
		System.out.println("Response Message : "+ responseMessage);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}