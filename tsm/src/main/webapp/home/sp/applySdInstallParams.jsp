<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div id="installParamsDiv" style="display: none">
	<form id="" title="installParamsClient" method="post">
		<button class="validate['submit']" style="display: none;"></button>
		<table class="openw">
			<tr>
				<td class="td1"><span style='color: red;'>*</span>安全域是否允许删除:&nbsp;</td>
				<td><select id="deleteSelf" name="deleteSelf">
						<option value="0" selected>允许</option>
						<option value="1">不允许</option>
				</select></td>
			</tr>
			<tr>
				<td class="td1"><span style='color: red;'>*</span>安全域是否接受迁移:&nbsp;</td>
				<td><select id="transfer" name="transfer">
						<option value="1" selected>接受</option>
						<option value="0">不接受</option>
				</select></td>
			</tr>
			<tr>
				<td class="td1"><span style='color: red;'>*</span>是否接受主安全域发起的应用删除:&nbsp;</td>
				<td><select id="deleteApp" name="deleteApp">
						<option value="0" selected>接受</option>
						<option value="1">不接受</option>
				</select></td>
			</tr>
			<tr>
				<td class="td1">空间管理模式:&nbsp;</td>
				<td colspan="3"><input name="spacePatten" title="spacePatten"
					type="checkbox" id="spacePatten" value="true" />签约空间模式<br /></td>
			</tr>
			<tr>
				<td class="td1">安全域管理的内存空间:&nbsp;</td>
				<td><input id="managedVolatileSpace" value=""
					class="inputtext validate['digit[1,65535]']"
					name="managedVolatileSpace" type="text" maxlength="5" disabled /></td>
				<td>byte</td>
			</tr>
			<tr>
				<td class="td1">安全域管理的存储空间:&nbsp;</td>
				<td colspan="3"><input id="managedNoneVolatileSpace" value=""
					class="inputtext validate['digit[1,4294967295]']"
					name="managedNoneVolatileSpace" type="text" maxlength="10" disabled />
				</td>
				<td>byte</td>
			</tr>
			<tr>
				<td class="td1">安全通道协议:&nbsp;</td>
				<td><select id="scp" name="scp">
						<option value="-1">选择通道协议</option>
						<option value="10,01">SCP10 0x01</option>
						<option value="02,15" selected="selected">SCP02 0x15</option>
						<option value="10,02">SCP10 0x02</option>
						<option value="02,05">SCP02 0x05</option>
						<option value="02,45">SCP02 0x45</option>
						<option value="02,55">SCP02 0x55</option>
				</select></td>
			</tr>
			<tr>
				<td class="td1">安全通道最大连续鉴权失败次数:&nbsp;</td>
				<td><input id="maxFailCount" value=""
					class="inputtext validate['digit[1,255]']" name="maxFailCount"
					type="text" maxlength="3" /></td>
			</tr>
			<tr>
				<td class="td1">密钥版本号:&nbsp;</td>
				<td><input id="keyVersion" value=""
					class="inputtext validate['digit[1,255]']" name="keyVersion"
					type="text" maxlength="3" /></td>
			</tr>
			<tr>
				<td class="td1">安全域支持的最大对称密钥个数:&nbsp;</td>
				<td><input id="maxKeyNumber" value=""
					class="inputtext validate['digit[1,255]']" name="maxKeyNumber"
					type="text" maxlength="3" /></td>
			</tr>
		</table>
	</form>
</div>