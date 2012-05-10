import demo.springsec.Role
import demo.springsec.User
import demo.springsec.UserRole
import demo.springsec.RequestMap

class BootStrap {

    def init = { servletContext ->

        ['ROLE_ADMIN', 'ROLE_USER'].each {
            if (!Role.findByAuthority(it)) {
                new Role(authority: it).save(failOnError: true)
            }

        }

        createUserWithRoles('admin', 'adminpw', ['ROLE_ADMIN'])
        createUserWithRoles('demouser', 'demopw', ['ROLE_USER'])
        [
                //'/**': 'ROLE_ADMIN', //TODO find the reason why this does not work
                '/static/**': 'IS_AUTHENTICATED_ANONYMOUSLY,IS_AUTHENTICATED_REMEMBERED',
                '/**/*.js': 'IS_AUTHENTICATED_ANONYMOUSLY,IS_AUTHENTICATED_REMEMBERED',
                '/**/*.css': 'IS_AUTHENTICATED_ANONYMOUSLY,IS_AUTHENTICATED_REMEMBERED',
                '/dbconsole/**': 'IS_AUTHENTICATED_ANONYMOUSLY,IS_AUTHENTICATED_REMEMBERED', //TODO: use ROLE_ADMIN later on
                '/login/**': 'IS_AUTHENTICATED_ANONYMOUSLY',
                '/logout/**': 'IS_AUTHENTICATED_REMEMBERED',

//                '/': 'IS_AUTHENTICATED_REMEMBERED',
                '/adminOnly/**': 'ROLE_ADMIN',
                '/userOnly/**': 'ROLE_USER',

                // s2-ui setup below
                '/admin/**': 'ROLE_ADMIN',
                '/user/**': 'ROLE_ADMIN',
                '/role/**': 'ROLE_ADMIN',
                '/requestmap/**': 'ROLE_ADMIN',
        ].each { url, role ->
            def requestMap = RequestMap.findByUrl(url) ?: new RequestMap()
            requestMap.url = url
            requestMap.configAttribute = role
            requestMap.save(failOnError: true)
        }


    }

    protected createUserWithRoles(String username, String password, roles) {
        if (!User.findByUsername(username)) {
            def user = new User(username: username, password: password, enabled: true)
            user.save(failOnError: true)
            roles.each {
                UserRole.create(user, Role.findByAuthority(it))
            }
        }
    }

    def destroy = {
    }
}
