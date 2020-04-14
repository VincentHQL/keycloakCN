<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "header">
        ${msg("loginProfileTitle")}
    <#elseif section = "form">
        <form id="kc-update-profile-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
            <#if user.editUsernameAllowed>
                <div class="${properties.kcFormGroupClass!} ${messagesPerField.printIfExists('username',properties.kcFormGroupErrorClass!)}">
                    <div class="${properties.kcLabelWrapperClass!}">
                        <label for="username" class="${properties.kcLabelClass!}">${msg("username")}</label>
                    </div>
                    <div class="${properties.kcInputWrapperClass!}">
                        <input type="text" id="username" name="username" value="${(user.username!'')}"
                               class="${properties.kcInputClass!}"/>
                    </div>
                </div>
            </#if>

            <div class="${properties.kcFormGroupClass!} ${messagesPerField.printIfExists('phoneNumber',properties.kcFormGroupErrorClass!)}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="phoneNumber" class="${properties.kcLabelClass!}">${msg("phoneNumber")}</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="text" id="phoneNumber" name="phoneNumber" value="${(user.attributes.phoneNumber!'')}"
                           class="${properties.kcInputClass!}"/>
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!} ${messagesPerField.printIfExists('verifyCode',properties.kcFormGroupErrorClass!)}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="verifyCode" class="${properties.kcLabelClass!}">${msg("verifyCode")}</label>
                </div>
                <div class="col-xs-8 col-sm-8 col-md-8 col-lg-8">
                    <input type="text" id="verifyCode" name="verifyCode" value="${(verifyCode!'')}"
                           class="${properties.kcInputClass!}"/>
                </div>
                <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
                    <button tabindex="4"
                            class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                            name="submitAction"
                            id="senCodeBtn"
                            type="submit"
                            value="doSendSmsCode">获取验证码
                    </button>
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                    <div class="${properties.kcFormOptionsWrapperClass!}">
                    </div>
                </div>

                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <#if isAppInitiatedAction??>
                        <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                               type="submit" value="${msg("doSubmit")}"/>
                        <button
                        class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonLargeClass!}"
                        type="submit" name="cancel-aia" value="true" />${msg("doCancel")}</button>
                    <#else>
                        <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                               type="submit" value="${msg("doSubmit")}"/>
                    </#if>
                </div>
            </div>
        </form>
    </#if>

    <#if sendCode??>
        <script>
            window.onload = function () {
                let getcodeBtn = document.querySelector("#senCodeBtn");
                getcodeBtn.disabled = "disabled";
                let a = 60;
                let code = setInterval(() => {
                    a--;
                    if (a > 0) {
                        getcodeBtn.innerHTML = a + "s"
                    }
                    else {
                        clearInterval(code);
                        getcodeBtn.disabled = "";
                        getcodeBtn.innerHTML = "获取验证码"
                    }
                }, 1000)
            }


        </script>
    </#if>
</@layout.registrationLayout>
