package com.ericsson.fms.filter;

import com.ericsson.fms.exception.http.ResponseData;
import com.ericsson.fms.utils.JsonObject;
import com.ericsson.fms.utils.PropUtil;
import sun.misc.BASE64Decoder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("restriction")
public class HTTPBasicAuthorizeAttribute implements Filter{
    private String Name;
    private String Password;
    
    @Override  
    public void destroy() {  
    }  
  
    @Override  
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)  
            throws IOException, ServletException {  
          
    	ResponseData resultStatusCode = checkHTTPBasicAuthorize(request);
        if (!resultStatusCode.getStatus().equals(0)){
            HttpServletResponse httpResponse = (HttpServletResponse) response;  
            httpResponse.setCharacterEncoding("UTF-8");    
            httpResponse.setContentType("application/json; charset=utf-8");   
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  
            httpResponse.getWriter().write(JsonObject.toJSONString(resultStatusCode));
            return;  
        } else {  
            chain.doFilter(request, response);  
        }  
    }  
  
    @Override  
    public void init(FilterConfig arg0) throws ServletException { 
    	Name = PropUtil.getProperty("gis.interface.name");
    	Password = PropUtil.getProperty("gis.interface.pwd");
    }  
      
    private ResponseData checkHTTPBasicAuthorize(ServletRequest request){  
    	ResponseData responseData = new ResponseData();
    	try  
        {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            String auth = httpRequest.getHeader("Authorization");  
            if ((auth != null) && (auth.length() > 6)){  
                String HeadStr = auth.substring(0, 5).toLowerCase();  
                if (HeadStr.compareTo("basic") == 0){  
                    auth = auth.substring(6, auth.length()); 
                    String decodedAuth = getFromBASE64(auth); 
                    if (decodedAuth != null){  
                        String[] UserArray = decodedAuth.split(":");  
                        if (UserArray != null && UserArray.length == 2){  
                            if (UserArray[0].compareTo(Name) == 0  
                                    && UserArray[1].compareTo(Password) == 0){ 
                                responseData.setMessage("Success");
                                responseData.setStatus(0);
                                return responseData;  
                            }  
                        }  
                    }  
                }  
            }  
            responseData.setStatus(500002);
            responseData.setMessage("Access request permission denied.");
            return responseData;
        }  
        catch(Exception ex){
            responseData.setStatus(500002);
            responseData.setMessage("Access request permission denied.");
            return responseData; 
        }  
          
    }  
      
    private String getFromBASE64(String s) {    
        if (s == null)    
            return null;    
        BASE64Decoder decoder = new BASE64Decoder();    
        try {    
            byte[] b = decoder.decodeBuffer(s);    
            return new String(b);    
        } catch (Exception e) {    
            return null;    
        }    
    }
}
