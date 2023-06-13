package pt.isel.iot_data_server.service.email

import org.json.JSONObject
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Component
class EmailManager {
    fun sendEmail(to: String, subject: Map<String, String>, body: Map<String, String>, templateName: String) {

        val sender = System.getenv("SENDER_EMAIL")
        val senderPassword = System.getenv("SENDER_PASS")

        val props = prepareProperties()
        // Create a Session object with SMTP authentication
        val session = prepareSession(props, sender, senderPassword)

        // Create a MimeMessage object with the email content
        val message = prepareMessage(session, to, subject, body, templateName, sender)
        // Send the email message
        Transport.send(message)
    }

    private fun prepareMessage(session: Session?, to: String, subject: Map<String,String>, body: Map<String,String>,templateName: String, sender:String): Message {
        val templateMessage = createMessageFromTemplate(templateName,subject,body)
        val message = MimeMessage(session)
        message.setFrom(InternetAddress(sender))
        message.setRecipient(Message.RecipientType.TO, InternetAddress(to))
        message.subject = templateMessage.first
        message.setText(templateMessage.second, "utf-8", "html")
        return message
    }

    private fun prepareProperties(): Properties {
        val props = Properties()
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.port"] = "587" // replace with your email provider's SMTP port
        props["mail.smtp.auth"] = "true" // enable SMTP authentication
        props["mail.smtp.starttls.enable"] = "true" // enable TLS encryption
        return props
    }

    private fun prepareSession(props: Properties, senderEmail:String, senderPassword:String): Session? {
        return Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(
                    senderEmail, //"testsubjectforiotproject@gmail.com",
                    senderPassword//"hesazkbnzoiahyaw"
                )
            }
        })
    }

    private fun createMessageFromTemplate(templateName : String, subjectDynamicContent: Map<String, String>, bodyDynamicContent: Map<String, String>):Pair<String,String> {
        val rootPath = System.getProperty("user.dir")
        val templatesJson = String(Files.readAllBytes(Paths.get("$rootPath/email_service_config/templateEmailMessages.json")))
        val templates = JSONObject(templatesJson)

        // Determine which template to use based on the conditions
        val template = templates.getJSONObject(templateName)

        // Replace the placeholders in the template with the dynamic content
        val subject = replacePlaceholders(template.getString("subject"), subjectDynamicContent)
        val body = replacePlaceholders(template.getString("body"), bodyDynamicContent)
        return Pair(subject,body)
    }

    private fun replacePlaceholders(input: String, map: Map<String, String>): String {
        var output = input
        map.forEach { (key, value) ->
            output = output.replace("{{${key}}}", value)
        }
        return output
    }
}