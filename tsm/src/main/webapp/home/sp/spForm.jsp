<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="regcont" style="overflow-x: hidden;">
		<dl>
			<dd>
				<p class="regtext">
					RID:
				</p>
				<p class="left inputs">
					<input tabindex="3" class="inputtext validate['required','length[10,10]','%checkHex']" maxlength="10" id="rid" name="rid" type="text" />
				</p>
			</dd>
			<dd>
				<p class="regtext">
					企业邮件地址:
				</p>
				<p class="left inputs">
					<input tabindex="1"
						class="inputtext validate['required','email','length[0,32]']"
						maxlength="32" id="email" name="email" type="text" />
				</p>
				<div>
					<p class="explain left">
					</p>
				</div>
			</dd>
			<dd>
				<p class="regtext">
					企业名称:
				</p>
				<p class="left inputs">
					<input tabindex="4"
						class="inputtext validate['required','length[1,50]','%checkFullName']"
						maxlength="50" id="name" name="name" type="text" />
				</p>
			</dd>
			<dd>
				<p class="regtext">
					企业简称:
				</p>
				<p class="left inputs">
					<input tabindex="5"
						class="inputtext validate['required','length[1,25]','%checkShortName']"
						maxlength="25" id="shortName" name="shortName" type="text" />
				</p>
			</dd>
			<dd>
				<p class="regtext">
					工商注册编号:
				</p>
				<p class="left inputs">
					<input tabindex="6"
						class="inputtext validate['required','length[1,32]','%illegalCharsCheck','%checkRegistrationNo']"
						maxlength="32" id="registrationNo" name="registrationNo"
						type="text" />
				</p>
				<p class="explain left"></p>
			</dd>
			<dd>
				<p class="regtext">
					经营许可证编号:
				</p>
				<p class="left inputs">
					<input tabindex="7"
						class="inputtext validate['required','length[1,32]','%illegalCharsCheck','%checkCertificateNo']"
						maxlength="32" id="certificateNo" name="certificateNo" type="text" />
				</p>
				<p class="explain left"></p>
			</dd>
			<dd>
				<p class="regtext">
					所在地:
				</p>
				<p class="left inputs">
					<input tabindex="8"
						class="inputtext validate['required','length[1,16]']"
						maxlength="16" name="locationNo" id="location" type="text" /> <input
						id="dns" type="hidden" name="dns" value=""></input>
				</p>
			</dd>
			<dd>
				<p class="regtext">应用提供商类型:</p>
				<p class="left inputs">
					<select tabindex="9" name="type">
						<option value="1">全网移动</option>
						<option value="2">本地移动</option>
						<option value="3">全网应用提供商</option>
						<option value="4">本地应用提供商</option>
					</select>
				</p>
			</dd>
			<dd>
				<p class="regtext">
					企业联系地址:
				</p>
				<p class="left inputs">
					<input tabindex="10"
						class="inputtext validate['required','length[1,120]']"
						maxlength="120" id="address" name="address" type="text" />
				</p>
			</dd>
			<dd>
				<p class="regtext">
					企业法人姓名:
				</p>
				<p class="left inputs">
					<input tabindex="11"
						class="inputtext validate['required','length[1,16]']"
						maxlength="16" id="legalPersonName" name="legalPersonName"
						type="text" />
				</p>
			</dd>
			<dd>
				<p class="regtext">
					法人证件类型:
				</p>
				<p class="left inputs">
					<input tabindex="12" class="inputradio" name="legalPersonIdType"
						type="radio" value="身份证" checked="checked" />身份证 <input
						class="inputradio" name="legalPersonIdType" type="radio"
						value="护照" />护照
				</p>
			</dd>
			<dd>
				<p class="regtext">
					法人证件号码:
				</p>
				<p class="left inputs">
					<input tabindex="13"
						class="inputtext validate['required','length[1,32]','%illegalCharsCheck','%checkLegalPersonIdNo']"
						maxlength="32" id="legalPersonIdNo" name="legalPersonIdNo"
						type="text" />
				</p>
				<p class="explain left"></p>
			</dd>
			<dd>
				<p class="regtext">
					企业性质:
				</p>
				<p class="left inputs">
					<select tabindex="14" name="firmNature"
						class="validate['required']">
						<option>请选择</option>
						<option value="1">国有</option>
						<option value="2">合作</option>
						<option value="3">合资</option>
						<option value="4">独资</option>
						<option value="5">集体</option>
						<option value="6">私营</option>
						<option value="7">个体工商户</option>
						<option value="8">报关</option>
						<option value="9">其他</option>
					</select>
				</p>
			</dd>
			<dd>
				<p class="regtext">
					企业规模:
				</p>
				<p class="left inputs">
					<select tabindex="15" name="firmScale" class="validate['required']">
						<option>请选择</option>
						<option value="1">小型(100人以下)</option>
						<option value="2">中型(100-500人)</option>
						<option value="3">大型(500人以上)</option>
					</select>
				</p>
			</dd>
	
			<dd id="pcIconUpload">
				<p class="regtext">企业LOGO:</p>
				<p class="left inputs">
					<img alt="" src="" id="firmLogo" name="firmLogo">
				</p>
			</dd>
			
			
			<dd id="dd_17">
				<p class="regtext">
					业务联系人姓名:
				</p>
				<p class="left inputs">
					<input tabindex="17"
						class="inputtext validate['required','length[1,16]']"
						maxlength="16" id="contactPersonName" name="contactPersonName"
						type="text" />
				</p>
			</dd>
			<dd id="dd_18">
				<p class="regtext">
					联系人手机号:
				</p>
				<p class="left inputs">
					<input tabindex="18"
						class="inputtext validate['required','length[11,11]','number','%checkContactPersonMobileNo']"
						maxlength="11" id="contactPersonMobileNo"
						name="contactPersonMobileNo" type="text" />
				</p>
			</dd>

		</dl>
</div>