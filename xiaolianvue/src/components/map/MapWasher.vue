<script setup>
const props=defineProps([
    "deviceInfo","avgWashTime","isexample"
])
function formatColor(device) {
    let s=device.status
    if(s==2&&props.avgWashTime<(new Date().getTime()-device.time)) return '#F1441D'
    if(s==1&&new Date().getTime()-device.wtime>360000) return '#3BC8B4'
    if (s == 1) return '#A9A9A9'
    else if (s == 2) return '#1661AB'
    else return '#FBDA41'
}
function fixedId(id){
    return id<10?"0"+id:id
}
</script>

<template>
    <div class="mapwasher_container" v-if="props.deviceInfo!=null">
        <div class="mapwasher_badge"  :style="{backgroundColor: formatColor(props.deviceInfo)}">
            {{ fixedId(props.deviceInfo.id) }}
        </div>
    </div>
    <div v-else-if="props.isexample" class="mapwasher_example_div">
        <div class="mapwasher_example">
            <div class="mapwasher_badge" :style="{backgroundColor: '#A9A9A9'}">
            00
            </div>
            未在使用
        </div>
        <div class="mapwasher_example">
            <div class="mapwasher_badge" :style="{backgroundColor: '#3BC8B4'}">
            01
            </div>
            应该无人
        </div>
        <div class="mapwasher_example">
            <div class="mapwasher_badge" :style="{backgroundColor: '#1661AB'}">
            02
            </div>
            正在使用
        </div>
        <div class="mapwasher_example">
            <div class="mapwasher_badge" :style="{backgroundColor: '#F1441D'}">
            03
            </div>
            洗了好久
        </div>
        <div class="mapwasher_example">
            <div class="mapwasher_badge" :style="{backgroundColor: '#FBDA41'}">
            04
            </div>
            发生故障
        </div>
    </div>
    <div class="mapwasher_badge" v-else></div>
</template>

<style>
.mapwasher_example{
    font-size: small;
    display: flex;
    flex-direction: column;
    align-items: center;
}
.mapwasher_example_div{
    display: flex;
    justify-content: space-evenly;
}
.mapwasher_badge{
    font-family: "Gilroy", sans-serif;
    vertical-align: middle;
    height: 20px;
    width: 20px;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 5px;
    color: #FFF;
    margin: 3px;
    font-size: small;
    transition: background-color 0.2s ease-in-out;
}
</style>