<
script >
function dec(mail) {
    mail = mail.replace(/.de/, "");
    mail = mail.replace(/sp/, "michael");
    mail = mail.replace(/am/, ".menzel");
    mail = mail.replace(/thm/, "mni.thm.de");
    mail = mail.replace(/\|/, "@");

    document.location.href = 'mailto:' + mail
}
< /script>;
