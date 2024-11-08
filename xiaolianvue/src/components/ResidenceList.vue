<script setup>
import wnetwork from '@/wnetwork';
import LineMdQuestionCircleTwotone from '~icons/line-md/question-circle-twotone?width=28px&height=28px';
import { ElOption, ElSelect } from 'element-plus';
var selectedValue = defineModel('residenceId')
var placeholderText = defineModel('residenceText')
var residences = defineModel("residences")
residences.value = []
wnetwork.get("/api/list")
    .then(response => {
        residences.value = response.data.data
        selectedValue.value = parseInt(sessionStorage.getItem("residenceId"))
        console.log(residences.value)
    })
function onChangeSelectedValue(value) {
    sessionStorage.setItem("residenceId", value)
    console.log(selectedValue)
    window.location.reload()
}
</script>

<template>
    <div class="residenceList">
        <div class="residenceList_tips">
            查询宿舍：
        </div>
        <div class="residenceList_select">
            <ElSelect v-model="selectedValue" placeholder="选择宿舍" style="width: 240px;" @change="onChangeSelectedValue">
                <ElOption v-for="residence in residences" :key="residence.residenceId" :value="residence.residenceId"
                    :label="residence.name" />
            </ElSelect>
        </div>

    </div>
    <div class="residenceList_notice">
        <LineMdQuestionCircleTwotone class="residenceList_notice_icon" />
        <div>
            没有你所在的宿舍？请联系作者：<br />
            抖音@海蓝色的咕咕鸽
            , 微信+13373223536,
            Github@aquamarine5
        </div>
    </div>
</template>

<style>
.residenceList_notice_icon {
    padding-right: 11px;
    padding-left: 8px;
    min-height: 28px;
    min-width: 28px;
}

.residenceList {
    background-color: #BDBDBD;
    display: flex;
    align-items: center;
    padding: 7px 7px 12px 14px;
    border-radius: 10px 10px;
}

.residenceList_select {
    display: flex;
    align-items: center;
    gap: 40px;
    flex-wrap: wrap;
}

.residenceList_notice {
    margin-top: -7px;
    font-weight: 400;
    color: white;
    font-size: smaller;
    background-color: rgb(83, 83, 83);
    display: flex;
    align-items: center;
    padding: 7px;
    border-radius: 0px 0px 10px 10px;
}
</style>