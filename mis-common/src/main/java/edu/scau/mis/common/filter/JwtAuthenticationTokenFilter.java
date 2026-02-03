package edu.scau.mis.common.filter;

import edu.scau.mis.common.domain.LoginUser;
import edu.scau.mis.common.utils.JwtUtil;
import edu.scau.mis.common.utils.RedisCache;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String url = request.getRequestURI();
        // 👇👇👇【新增/修改这里】扩充白名单 👇👇👇
        // 凡是下面这些路径，直接放行，不要检查 Token，也不要打印日志干扰视线
        if (// 1. 登录注册相关
                url.contains("/auth/login") ||
                        url.contains("/auth/register") ||
                        url.contains("/auth/code") ||
                        url.contains("/common/upload") ||
                        // 2. Swagger/Knife4j 相关资源
                        url.contains("/doc.html") ||
                        url.contains("/webjars/") ||
                        url.contains("/v3/api-docs") ||
                        url.contains("/swagger-resources") ||
                        url.contains("/favicon.ico")||
                        url.contains("/unlockStock")) {

            // 直接放行，return 结束当前过滤器的逻辑
            filterChain.doFilter(request, response);
            return;
        }
        // 👆👆👆【白名单结束】👆👆👆

        System.out.println("========== 过滤器开始执行: " + url + " ==========");

        // 1. 检查 Header
        String token = request.getHeader("Authorization");
        System.out.println("1. 收到 Authorization 头: " + token);

        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            System.out.println("❌ 失败：Token为空或格式不对(没带Bearer空格)");
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);

        String userId;
        try {
            Claims claims = jwtUtil.parseJWT(token);
            userId = claims.getSubject();
            System.out.println("3. Token 解析成功，UserId: " + userId);
        } catch (Exception e) {
            System.out.println("❌ 失败：Token 解析报错: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // 4. 查 Redis
        String redisKey = "login:" + userId;
        // 打印一下 key，看看有没有空格或者奇怪的字符
        System.out.println("4. 准备查询 Redis Key: [" + redisKey + "]");

        redisCache.expire(redisKey, 30, TimeUnit.MINUTES);

        LoginUser loginUser = redisCache.getCacheObject(redisKey);

        if (Objects.isNull(loginUser)) {
            System.out.println("❌ 失败：Redis 里没查到数据！(过期或 Key 不匹配)");
            renderError(response, "登录状态已过期，请重新登录");
            return;
        }

        System.out.println("✅ 成功：Redis 查到了用户: " + loginUser.getUsername());

        // ... 后续逻辑 ...
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
    /**
     * 手动拼装 JSON 并写回给前端
     * 这种方式不依赖 ApiResult 也不依赖 Jackson，绝对不会报错
     */
    private void renderError(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(200); // 建议用 200，业务码用 401
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        // 手动拼 JSON 字符串：{"code": 401, "message": "msg", "data": null}
        // 注意转义双引号
        String json = "{\"code\": 401, \"message\": \"" + msg + "\", \"data\": null}";

        response.getWriter().print(json);
    }
}
