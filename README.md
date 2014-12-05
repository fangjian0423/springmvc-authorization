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

##实例##

###类上的使用###

    @RequestMapping("/user")
    @Controller
    @Authorization(roles = {"admin"})
    public class UserController {
        @RequestMapping("/add")
        public String add() {
            return "user";
        }
    }
以/user为前缀的地址的访问权限是用户"admin"角色的用户

    @RequestMapping("/user")
    @Controller
    @Authorization(auth = {"user_manager"})
    public class UserController {
        @RequestMapping("/add")
        public String add() {
            return "user";
        }
    }
以/user为前缀的地址的访问权限是拥有"user_manager"权限的用户


###方法上的使用###

    @RequestMapping("/user")
    @Controller
    public class UserController {
        @RequestMapping("/add")
        @Authorization(roles = {"admin"})
        public String add() {
            return "user";
        }
        @RequestMapping("/delete")
        public String delete() {
            return "user";
        }
    }
/user/add地址的访问权限是拥有"admin"角色的用户，/user/delete地址无权限限制

    @RequestMapping("/user")
    @Controller
    public class UserController {
        @RequestMapping("/add")
        @Authorization(auth = {"add_user"})
        public String add() {
            return "user";
        }
    }
/user/add地址的访问权限是拥有"add_user"权限的用户

###类和方法上的使用###

    @RequestMapping("/user")
    @Controller
    @Authorization(roles = {"manager1"})
    public class UserController {
        @RequestMapping("/add")
        @Authorization(roles = {"manager2"})
        public String add() {
            return "user";
        }
    }

/user/add地址的访问权限是拥有"manager1"和"manager2"这2个角色的用户


###编写自定义的权限验证逻辑###

实现AuthHandler接口和Spring的Ordered接口，还要加上@Component注解或写在spring配置文件中，默认的AuthHandler实现类是DefaultAuthHandler，他的order值为0