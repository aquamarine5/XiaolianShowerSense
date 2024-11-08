<script setup>
import MapRoad from './MapRoad.vue';
import wnetwork from '@/wnetwork';
import { computed, ref } from 'vue';
import LineMdMapMarkerRadiusTwotone from '~icons/line-md/map-marker-radius-twotone?width=28px&height=28px';
import LineMdEmojiFrownTwotone from '~icons/line-md/emoji-frown-twotone?width=28px&height=28px';
var isShowMap = ref(false)
var roadMap
const props = defineProps([
    "devicesList","avgWashTime"
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
    console.log(props.devicesList)
    return newMapData;
})
const isReady=computed(()=>{
    console.log(props.devicesList.length)
    return props.devicesList.length!=0
})
</script>

<template>
    <div class="map_container">
        <div class="map_view" v-if="isShowMap">
            <div class="map_title">
                <LineMdMapMarkerRadiusTwotone/>
                地图：
            </div>
            <MapRoad v-for="(road, index) in mapData" :roadData="road" :avgWashTime="props.avgWashTime" :isready="isReady"/>
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
.map_title{
    display: flex;
    align-items: center;
    padding-block: 5px;
}
.map_container {
    width: 100%;
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
    width: 100%;
    gap: 10px;
}
.map_nodata_text {
    padding-inline: 7px;
    font-size: medium;
}
</style>