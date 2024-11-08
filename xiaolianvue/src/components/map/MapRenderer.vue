<script setup>
import MapRoad from './MapRoad.vue';
import wnetwork from '@/wnetwork';
import { computed, ref } from 'vue';
import LineMdFolderPlusTwotone from '~icons/line-md/folder-plus-twotone?width=28px&height=28px';
import LineMdMapMarkerRadiusTwotone from '~icons/line-md/map-marker-radius-twotone?width=28px&height=28px';
import LineMdEmojiFrownTwotone from '~icons/line-md/emoji-frown-twotone?width=28px&height=28px';
import MapWasher from './MapWasher.vue';
var isShowMap = ref(false)
var roadMap
var contributors = ref("")
const props = defineProps([
    "devicesList", "avgWashTime"
])
wnetwork.get("/api/map?id=" + sessionStorage.getItem("residenceId"))
    .then(response => {
        isShowMap.value = response.data.data.isShowMap
        roadMap = response.data.data.mapdata
        contributors.value = "地图数据由：" + response.data.data.contributors + " 提供。"
    })
const mapData = computed(() => {
    if (isShowMap.value == false) return []
    let newMapData = []
    roadMap.forEach(element => {
        let roadData = [[], []]
        if (props.devicesList.value == []) return [];
        element[0].forEach(displayNo => {
            roadData[0].push(displayNo != -1 ? props.devicesList[displayNo - 1] : null)
        });
        element[1].forEach(displayNo => {
            roadData[1].push(displayNo != -1 ? props.devicesList[displayNo - 1] : null)
        });
        newMapData.push(roadData)
    });
    console.log(newMapData)
    console.log(props.devicesList)
    return newMapData;
})
const isReady = computed(() => {
    console.log(props.devicesList.length)
    return props.devicesList.length != 0
})
</script>

<template>
    <div class="map_container">
        <div class="map_view" v-if="isShowMap">
            <div class="map_title">
                <LineMdMapMarkerRadiusTwotone />
                浴室地图：
            </div>
            <MapRoad v-for="(road, index) in mapData" :roadData="road" :avgWashTime="props.avgWashTime"
                :isready="isReady" :key="index"/>
        </div>
        <div class="map_nodata" v-else>
            <div class="map_nodata_tips">
                <LineMdEmojiFrownTwotone class="map_nodata_icon" />
                <div class="map_nodata_text">
                    Oops! 该宿舍暂无地图数据😢
                </div>
            </div>
            <div class="map_nodata_tips">
                <LineMdFolderPlusTwotone class="map_nodata_icon" />
                <div class="map_nodata_text">
                    不过没关系，你可以帮助我们完善地图数据😚
                </div>
            </div>

        </div>
        <div class="map_contributors" v-if="isShowMap">
            {{ contributors }}
        </div>
        <div class="map_splitline"></div>
        <div class="map_example">
            <MapWasher :isexample="true" />
        </div>
    </div>
</template>


<style>
.map_splitline {
    background-color: rgb(180, 180, 180);
    border-radius: 99px;
    opacity: 0.5;
    margin-block: 8px;
    height: 1.5px;
    width: 95%;
}

.map_contributors {
    font-size: small;
    color: gray;
    width: 100%;
    text-align: left;
    padding-top: 5px;
}

.map_example {
    width: 100%;
}

.map_title {
    gap: 8px;
    display: flex;
    align-items: center;
    padding-bottom: 7px;
    font-weight: 600;
}

.map_container {
    flex-direction: column;
    width: 100%;
    display: flex;
    border-width: 5px;
    border-style: solid;
    align-items: center;
    border-color: transparent;
    background-image: linear-gradient(to right, #fff, #fff), linear-gradient(135deg, #A9C9FF, #FFBBEC);
    background-clip: padding-box, border-box;
    background-origin: padding-box, border-box;
    border-radius: 10px;
    padding: 8px 12px;
    box-sizing: border-box;
}

.map_view {
    width: 100%;
    gap: 10px;
}

.map_nodata_tips {
    display: flex;
    align-items: center;
}

.map_nodata_text {
    padding-inline: 7px;
    font-size: medium;
}

.map_nodata_icon {

    min-height: 28px;
    min-width: 28px;
}
</style>