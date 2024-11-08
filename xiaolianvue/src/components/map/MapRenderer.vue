<script setup>
import MapRoad from './MapRoad.vue';
import wnetwork from '@/wnetwork';
import { computed, ref } from 'vue';
import LineMdMapMarkerRadiusTwotone from '~icons/line-md/map-marker-radius-twotone?width=28px&height=28px';
import LineMdEmojiFrownTwotone from '~icons/line-md/emoji-frown-twotone?width=28px&height=28px';
var isShowMap = ref(false)
var roadMap
const props = defineProps([
    "devicesList"
])
wnetwork.get("/api/map?id=" + sessionStorage.getItem("residenceId"))
    .then(response => {
        isShowMap.value = response.data.data != null
        roadMap = response.data.data
    })
const mapData = computed(() => {
    let newMapData=[]
    roadMap.forEach(element => {
        let roadData = [[], []]
        console.log(props.devicesList)
        if(props.devicesList.value==[]) return [];
        element[0].forEach(displayNo => {
            roadData[0].push(displayNo!=-1?props.devicesList[displayNo - 1]:null)
        });
        element[1].forEach(displayNo => {
            roadData[1].push(displayNo!=-1?props.devicesList[displayNo - 1]:null)
        });
        newMapData.push(roadData)
    });
    console.log(newMapData)
    return newMapData;
})
</script>

<template>
    <div class="map_container">
        <div class="map_view" v-if="isShowMap">
            <LineMdMapMarkerRadiusTwotone/>
            <MapRoad v-for="(road, index) in mapData" :roadData="road" :ref="'maproad_' + index" :isReady="true"/>
        </div>
        <div class="map_nodata" v-else>
            <LineMdEmojiFrownTwotone class="map_nodata_icon" />
            <div class="map_nodata_text">
                Oops! 该宿舍暂无地图数据
            </div>
        </div>
    </div>
</template>


<style>
.map_container {
    display: flex;
    border-width: 3px;
    border-style: solid;
    border-radius: 7px;
    align-items: center;
    border-color: transparent;
    background-image: linear-gradient(to right, #fff, #fff), linear-gradient(135deg, #A9C9FF, #FFBBEC);
    background-clip: padding-box, border-box;
    background-origin: padding-box, border-box;
    padding: 5px 5px 5px 10px;
}
.map_view {
    gap: 10px;
}
.map_nodata_text {
    padding-inline: 7px;
    font-size: medium;
}
</style>