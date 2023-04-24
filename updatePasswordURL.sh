#!/bin/bash
# 将 <YourAppToken> 替换为你在服务端生成的 App Token，<YourPassword> 替换为你设置的新密码

client_id=""
client_secret=""

getToken(){
   curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' -d '{
   "grant_type": "client_credentials",
   "client_id": "YXA6NCizWK8ZTiKLcY-NJwAgbg",
   "client_secret": "YXA6Dzj9HhxCT376B-IgZAU9ACrL8wA",
   "ttl": "1024000"
}' 'http://a1.easemob.com/1135230423163966/demo/token'
}

# {"application":"3428b358-af19-4e22-8b71-8f8d2700206e","access_token":"YWMtbfRn6uKwEe2GJEFppWaE_2PD2rdcAz8QsxFDvusmk8E0KLNYrxlOIotxj40nACBuAgMAAAGHs8Wx2AAPoABZ68C3-cSVkEnZN0-oQiTvGqt4PI2xwamTGOR3oSfpeA","expires_in":1024000}

updatePassword(){
   YourAppToken="YWMtbfRn6uKwEe2GJEFppWaE_2PD2rdcAz8QsxFDvusmk8E0KLNYrxlOIotxj40nACBuAgMAAAGHs8Wx2AAPoABZ68C3-cSVkEnZN0-oQiTvGqt4PI2xwamTGOR3oSfpeA"
   newpassword="111"
   curl -X PUT -H 'Content-Type: application/json' -H 'Accept: application/json' -H "Authorization: Bearer $YourAppToken" -d '{ "newpassword": "111" }' 'https://a1.easemob.com/1135230423163966/demo/users/root/password'
}

#getToken
#updatePassword
