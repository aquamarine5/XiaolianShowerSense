<script setup>
import Device from './components/Device.vue';
import SuggestedDevice from './components/SuggestedDevice.vue';
import ResidenceList from './components/ResidenceList.vue';
import Topbar from './components/Topbar.vue';
import Introduction from './components/Introduction.vue';
import Sponsor from './components/Sponsor.vue';
import WarningNotRunning from './components/WarningNotRunning.vue';
import Map from './components/map/Map.vue';
import wnetwork from './wnetwork';
import { ref } from 'vue';

var washCount = ref(0)
var avgWashTimeText = ref("")
var requestTimes = ref(0)
var devicesList = []
var suggestedWaitDevicesList = []
var suggestedTryDevicesList = []

var residenceId = sessionStorage.getItem("residenceId")
if (residenceId == null) {
    residenceId = 1215856
    sessionStorage.setItem("residenceId", residenceId.toString())
}

var showTryMoreStatus = ref(false)
var showWaitMoreStatus = ref(false)
</script>

<template>
    <Topbar :wash-avg-time="avgWashTimeText" :use-count="requestTimes" :wash-count="washCount" />
    <div style="margin: 10px;">
        <ResidenceList />
        <Introduction />
        <WarningNotRunning />
        <Map ref="map" />
        <div class="top_container">
            <div class="suggested_tips">
                推荐去尝试可能没人的淋浴头：
                <div class="suggested_detail">
                    以下淋浴头已经很久没有人用啦 可能被遗忘了🤔
                </div>
            </div>
            <div class="suggested_content">
                <div class="suggested_container" v-for="device in suggestedTryDevicesList.slice(0, 6)">
                    <SuggestedDevice :name="device.name" :id="device.id" :status="device.status" :tme="device.wtime"
                        :key="device.id" />
                </div>
                <div class="suggested_more_container" v-if="showTryMoreStatus">
                    <div class="suggested_container" v-for="mdevice in suggestedTryDevicesList.slice(6, 20)">
                        <SuggestedDevice :name="mdevice.name" :id="mdevice.id" :status="mdevice.status"
                            :tme="mdevice.wtime" :key="mdevice.id" />
                    </div>
                </div>
                <div class="suggested_more_btn" @click="showTryMoreStatus = !showTryMoreStatus">
                    {{ showTryMoreStatus ? "收起" : "展开" }}
                </div>
            </div>

        </div>
        <div class="top_container">
            <div class="suggested_tips">
                推荐去尝试马上使用完毕的淋浴头：
                <div class="suggested_detail">
                    以下淋浴头已经洗了好久啦 大概率要洗完了🤓
                </div>
            </div>
            <div class="suggested_content">
                <div class="suggested_container" v-for="device in suggestedWaitDevicesList.slice(0, 6)">
                    <SuggestedDevice :name="device.name" :id="device.id" :status="device.status" :tme="device.wtime"
                        :key="device.id" />
                </div>
                <div class="suggested_more_container" v-if="showWaitMoreStatus">
                    <div class="suggested_container" v-for="mdevice in suggestedWaitDevicesList.slice(6, 20)">
                        <SuggestedDevice :name="mdevice.name" :id="mdevice.id" :status="mdevice.status"
                            :tme="mdevice.wtime" :key="mdevice.id" />
                    </div>
                </div>
                <div class="suggested_more_btn" @click="showWaitMoreStatus = !showWaitMoreStatus">
                    {{ showWaitMoreStatus ? "收起" : "展开" }}
                </div>
            </div>

        </div>
        <Sponsor />
        <div class="app_container">
            <div class="device_container" v-for="device in devicesList">
                <Device :name="device.name" :id="device.id" :status="device.status" :tme="device.time"
                    :wtime="device.wtime" />
            </div>
        </div>
    </div>

</template>

<script>
export default {
    created() {
        this.getDevices()
        let timer=setInterval(() => {
            this.refreshDevices()
        }, 10000)
        return{
            timer
        }
    },
    beforeDestroy() {
        clearInterval(this.timer)
    },
    methods: {
        formatDate(t) {
            var seconds = Math.floor((t / 1000) % 60),
                minutes = Math.floor((t / (1000 * 60)) % 60),
                hours = Math.floor((t / (1000 * 60 * 60)) % 24)

            hours = (hours < 10) ? "0" + hours : hours
            minutes = (minutes < 10) ? "0" + minutes : minutes
            seconds = (seconds < 10) ? "0" + seconds : seconds
            if (hours == "00")
                return minutes + " 分 " + seconds + " 秒"
            return hours + " 时 " + minutes + " 分 " + seconds + " 秒"
        },
        getDevices() {
            wnetwork.get("/api/wash?id=" + sessionStorage.getItem("residenceId"))
                .then(response => {
                    var json = response.data.data
                    var out = []
                    json["devices"].forEach(element => {
                        out[element.id - 1] = {
                            id: element.id,
                            status: element.status,
                            time: element.time,
                            name: element.name,
                            wtime: element.wtime
                        }
                    })
                    washCount.value = json.avgWashCount
                    avgWashTimeText.value = formatDate(json.avgWashTime)
                    requestTimes.value = json.requestTimes
                    console.log(out)
                    this.$refs.map.setupData(json)
                    devicesList = out;
                    out.forEach(element => {
                        if (element.status == 1 && (new Date().getTime() - element.wtime) > 360000) {
                            suggestedTryDevicesList.push(element)
                        }
                        if (element.status == 2 && (new Date().getTime() - element.time) > json.avgWashTime) {
                            suggestedWaitDevicesList.push(element)
                        }
                    })
                    suggestedTryDevicesList = suggestedTryDevicesList.sort((a, b) => a.wtime / 1000 - b.wtime / 1000).slice(0, 20)
                    suggestedWaitDevicesList = suggestedWaitDevicesList.sort((a, b) => a.time / 1000 - b.time / 1000).slice(0, 20)
                }).catch(function (err) {
                    console.log(err)
                    return [];
                })
        },
        refreshDevices() {
            var hour = new Date().getHours()
            if (hour < 13 || hour > 23) return;
            wnetwork.get("/api/refresh?id=" + sessionStorage.getItem("residenceId"))
                .then(response => {
                    var json = response.data.data
                    json["devices"].forEach(element => {
                        devicesList[element.id - 1].status = element.status
                        devicesList[element.id - 1].time = element.time
                    })
                    washCount.value = json.avgWashCount
                    avgWashTimeText.value = formatDate(json.avgWashTime)
                    requestTimes.value = json.requestTimes
                    suggestedTryDevicesList = []
                    suggestedWaitDevicesList = []
                    devicesList.forEach(element => {
                        if (element.status == 1 && (new Date().getTime() - element.wtime) > 360000) {
                            suggestedTryDevicesList.push(element)
                        }
                        if (element.status == 2 && (new Date().getTime() - element.time) > json.avgWashTime) {
                            suggestedWaitDevicesList.push(element)
                        }
                    })
                    suggestedTryDevicesList = suggestedTryDevicesList.sort((a, b) => a.wtime - b.wtime).slice(0, 20)
                    suggestedWaitDevicesList = suggestedWaitDevicesList.sort((a, b) => a.time - b.time).slice(0, 20)

                    console.log("refresh devices")
                }).catch(function (err) {
                    console.log(err)
                    return out;
                })
        }
    }
}
</script>

<style>
.suggested_detail {
    font-weight: 400;
    font-size: 12px;
}

.suggested_tips {
    border-radius: 7px 7px 0px 0px;
    border-style: solid;
    border-color: #0097a7;
    border-width: 0px 0px 3px 0px;
    background-color: #00bcd4;
    color: white;
    padding: 5px 5px 5px 14px;
    font-weight: 600;
}

.suggested_content {
    padding: 5px 5px 5px 14px;
}

.top_container {
    border-radius: 10px;
    border-color: #0097a7;
    border-style: solid;
    border-width: 3px;
    margin-block: 8px;
}

.suggested_more_btn {
    cursor: pointer;
    border-radius: 5px;
    padding: 0px 1px;
    border-width: 2px;
    border-style: solid;
    width: fit-content;
    border-color: gray;
    margin-block: 3px;
}

.app_container {
    padding-top: 10px;
}
</style>