<script setup>
import { ref } from 'vue';
import MapWasher from './MapWasher.vue';


const props=defineProps({
    roadData:{
        
    }
})
var washerDetails={
    left:[],
    right:[]
}
bus.$on("getData",out=>{
    for (let index = 0; index < props.roadData[0].length; index++) {
        washerDetails.left[index]=ref(out[props.roadData[0][index]])
    }
    for (let index = 0; index < props.roadData[1].length; index++) {
        washerDetails.right[index]=ref(out[props.roadData[1][index]])
    }
})

</script>

<template>
    <div class="road_container">
        <div class="road_neighbours">
            <MapWasher v-for="washer in washerDetails.left" :id="washer"/>
        </div>
        <div class="road_display">

        </div>
        <div class="road_neighbours">
            <MapWasher v-for="washer in washerDetails.right" :id="washer"/>
        </div>
    </div>
</template>