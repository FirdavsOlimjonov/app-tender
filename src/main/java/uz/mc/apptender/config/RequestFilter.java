//package uz.mc.apptender.config;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class RequestFilter implements Filter {
//
//    private static final Map<String, Integer> REQUEST_LIMITS_BY_METHOD_SHORT_TIME = new HashMap<>();
//    private static final Map<String, Integer> REQUEST_LIMITS_BY_METHOD_LONG_TIME = new HashMap<>();
//
//    private static final int BLOCKING_DURATION_SHORT_TIME = 2;
//    private static final int BLOCKING_DURATION_LONG_TIME = 15;
//
//    private static LoadingCache<String, Map<String, Integer>> requestCountsPerIpAddressShortTime;
//    private static LoadingCache<String, Map<String, Integer>> requestCountsPerIpAddressLongTime;
//
//    public RequestFilter() {
//        super();
//        REQUEST_LIMITS_BY_METHOD_SHORT_TIME.put(RestConstants.GET, 200);
//        REQUEST_LIMITS_BY_METHOD_SHORT_TIME.put(RestConstants.POST, 20);
//        REQUEST_LIMITS_BY_METHOD_SHORT_TIME.put(RestConstants.PATCH, 20);
//        REQUEST_LIMITS_BY_METHOD_SHORT_TIME.put(RestConstants.PUT, 20);
//        REQUEST_LIMITS_BY_METHOD_SHORT_TIME.put(RestConstants.DELETE, 20);
//        REQUEST_LIMITS_BY_METHOD_SHORT_TIME.put(RestConstants.OPTIONS, 200);
//        requestCountsPerIpAddressShortTime = CacheBuilder
//                .newBuilder()
//                .expireAfterWrite(BLOCKING_DURATION_SHORT_TIME, TimeUnit.SECONDS)
//                .build(new CacheLoader<String, Map<String, Integer>>() {
//                    public Map<String, Integer> load(String key) {
//                        return new HashMap<>();
//                    }
//                });
//
//        REQUEST_LIMITS_BY_METHOD_LONG_TIME.put(RestConstants.GET, 1000);
//        REQUEST_LIMITS_BY_METHOD_LONG_TIME.put(RestConstants.POST, 100);
//        REQUEST_LIMITS_BY_METHOD_LONG_TIME.put(RestConstants.PATCH, 100);
//        REQUEST_LIMITS_BY_METHOD_LONG_TIME.put(RestConstants.PUT, 100);
//        REQUEST_LIMITS_BY_METHOD_LONG_TIME.put(RestConstants.DELETE, 100);
//        REQUEST_LIMITS_BY_METHOD_LONG_TIME.put(RestConstants.OPTIONS, 1000);
//        requestCountsPerIpAddressLongTime = CacheBuilder
//                .newBuilder().
//                expireAfterWrite(BLOCKING_DURATION_SHORT_TIME, TimeUnit.SECONDS)
//                .build(new CacheLoader<String, Map<String, Integer>>() {
//                    public Map<String, Integer> load(String key) {
//                        return new HashMap<>();
//                    }
//                });
//    }
//
//
//    public Map<String, String> getClientIP(HttpServletRequest request) {
//        String remoteAddr = request.getRemoteAddr();
//
//        Map<String, String> map = new HashMap<>();
//        map.put(RestConstants.METHOD, Objects.requireNonNull(request.getMethod()));
//        map.put(RestConstants.IP_ADDRESS, Objects.requireNonNull(request.getRemoteAddr()));
//        return map;
//    }
//
//
//    @Override
//    public void init(FilterConfig filterConfig) {
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
//        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
//
//        Map<String, String> clientIP = getClientIP(httpServletRequest);
//
//        if (isMaximumRequestsPerSecondExceeded(clientIP)) {
//            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
//            httpServletResponse.getWriter().write("Too many requests");
//            return;
//        }
//        filterChain.doFilter(httpServletRequest, httpServletResponse);
//    }
//
//    @Override
//    public void destroy() {
//    }
//
//    private boolean isMaximumRequestsPerSecondExceeded(Map<String, String> map) {
//
//        return isMaximumRequestsPerSecondExceededShortTime(map) ||
//                isMaximumRequestsPerSecondExceededLongTime(map);
//
//    }
//
//
//    private boolean isMaximumRequestsPerSecondExceededShortTime(Map<String, String> map) {
//
//        Integer requestCount;
//        Map<String, Integer> allRequestsThisIpAddress;
//        try {
//            allRequestsThisIpAddress = requestCountsPerIpAddressShortTime.get(map.get(RestConstants.IP_ADDRESS));
//            requestCount = allRequestsThisIpAddress.getOrDefault(map.get(RestConstants.METHOD), 0);
//            requestCount++;
//            if (requestCount > REQUEST_LIMITS_BY_METHOD_SHORT_TIME.get(map.get(RestConstants.METHOD))) {
//                allRequestsThisIpAddress.put(map.get(RestConstants.METHOD), requestCount);
//                requestCountsPerIpAddressShortTime.put(map.get(RestConstants.IP_ADDRESS), allRequestsThisIpAddress);
//                return true;
//            }
//        } catch (ExecutionException e) {
//            return false;
//        }
//        allRequestsThisIpAddress.put(map.get(RestConstants.METHOD), requestCount);
//        requestCountsPerIpAddressShortTime.put(map.get(RestConstants.IP_ADDRESS), allRequestsThisIpAddress);
//        return false;
//    }
//
//    private boolean isMaximumRequestsPerSecondExceededLongTime(Map<String, String> map) {
//
//        Integer requestCount;
//        Map<String, Integer> allRequestsThisIpAddress;
//        try {
//            allRequestsThisIpAddress = requestCountsPerIpAddressLongTime.get(map.get(RestConstants.IP_ADDRESS));
//            requestCount = allRequestsThisIpAddress.getOrDefault(map.get(RestConstants.METHOD), 0);
//            requestCount++;
//            if (requestCount > REQUEST_LIMITS_BY_METHOD_LONG_TIME.get(map.get(RestConstants.METHOD))) {
//                allRequestsThisIpAddress.put(map.get(RestConstants.METHOD), requestCount);
//                requestCountsPerIpAddressLongTime.put(map.get(RestConstants.IP_ADDRESS), allRequestsThisIpAddress);
//                return true;
//            }
//        } catch (ExecutionException e) {
//            return false;
//        }
//        allRequestsThisIpAddress.put(map.get(RestConstants.METHOD), requestCount);
//        requestCountsPerIpAddressLongTime.put(map.get(RestConstants.IP_ADDRESS), allRequestsThisIpAddress);
//        return false;
//    }
//
//
//}
