springmvc-authorization
=======================

SpringMVC简单的权限框架

##主要原理##
    1. 有个BeanPostProcessor接口实现类，该类遍历所有的Controller，并根据类和方法上的@Authorization注解得到该链接对应的权限
    2. 有了对应的链接和权限之后，使用反射得到RequestMappingHandlerMapping的拦截器属性，并加入新的拦截去AuthInterceptor，该拦截器就是对权限的判断
    3. 注解@Authorization有两个属性，roles数组和auth数组，分别表示角色和权限(最终都会转换成权限，然后判断用户是否拥有该权限)

##其他##
    1. 请求如果是跳转(forward),那么权限不足的话会跳转到权限不足的页面
    2. 请求如果是服务器端写回数据，那么权限不足的话会写回json数据