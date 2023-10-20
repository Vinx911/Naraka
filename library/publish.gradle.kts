apply(plugin = "maven-publish")

plugins.withType<MavenPublishPlugin> {
    configure<PublishingExtension> {
        repositories {
            // 定义一个 maven 仓库
            maven {
                val repositoryUrl: String by project
                val repositoryUserName: String by project
                val repositoryPassword: String by project

                isAllowInsecureProtocol = true
                setUrl(repositoryUrl)
                // 仓库用户名密码
                credentials {
                    username = repositoryUserName
                    password = repositoryPassword
                }
            }
        }

        publications {
            create<MavenPublication>("maven") {
                groupId = "com.vinx911.naraka"
                artifactId = "Naraka"
                version = "1.0.0"
                description = "每一个App死掉(崩溃)后，都应该进入地狱(Naraka)."

                afterEvaluate {
                    from(components["release"])
                }
            }
        }
    }
}